/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2020, 2019-2020
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.patchworkmc.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.server.dedicated.DedicatedServer;

import net.fabricmc.loader.api.FabricLoader;

import net.patchworkmc.api.ForgeInitializer;
import net.patchworkmc.impl.registries.RegistryEventDispatcher;

public class Patchwork {
	private static final Logger LOGGER = LogManager.getLogger(Patchwork.class);

	private static void dispatch(Collection<FMLModContainer> mods, Event event) {
		dispatch(mods, container -> event);
	}

	/**
	 * Fire the specific event for all ModContainers on the {@link Mod.EventBusSubscriber.Bus.MOD} Event bus.
	 */
	private static void dispatch(Collection<FMLModContainer> mods, Function<ModContainer, Event> provider) {
		for (FMLModContainer container : mods) {
			ModLoadingContext.get().setActiveContainer(container, new FMLJavaModLoadingContext(container));

			Event event = provider.apply(container);
			LOGGER.debug("Firing event for modid {} : {}", container.getModId(), event.toString());
			container.patchwork$acceptEvent(event);
			LOGGER.debug("Fired event for modid {} : {}", container.getModId(), event.toString());

			ModLoadingContext.get().setActiveContainer(null, "minecraft");
		}
	}

	public static void gatherAndInitializeMods() {
		ForgeRegistries.init();

		Map<ForgeInitializer, FMLModContainer> mods = new HashMap<>();

		// Construct forge mods

		List<ForgeInitializer> entrypoints;

		try {
			entrypoints = FabricLoader.getInstance().getEntrypoints("patchwork", ForgeInitializer.class);
		} catch (Throwable t) {
			throw new PatchworkInitializationException("Failed to get Patchwork entrypoints!", t);
		}

		PatchworkInitializationException error = null;

		for (ForgeInitializer initializer : entrypoints) {
			LOGGER.info("Constructing Patchwork mod: " + initializer.getModId());

			FMLModContainer container = new FMLModContainer(initializer.getModId());
			ModLoadingContext.get().setActiveContainer(container, new FMLJavaModLoadingContext(container));

			try {
				// container.setMod()
				initializer.onForgeInitialize();
			} catch (Throwable t) {
				if (error == null) {
					error = new PatchworkInitializationException("Failed to construct Patchwork mods");
				}

				Throwable checked;

				if (t instanceof BootstrapMethodError) {
					checked = t.getCause();
				} else {
					checked = t;
				}

				if (checked instanceof NoClassDefFoundError || checked instanceof NoSuchMethodError || checked instanceof NoSuchFieldError) {
					final String unDefinedClass = checked.getMessage().substring(checked.getMessage().lastIndexOf(' ') + 1).replace('/', '.');
					String type;

					if (checked instanceof NoClassDefFoundError) {
						type = "class";
					} else if (checked instanceof NoSuchMethodError) {
						type = "method";
					} else {
						type = "field";
					}

					if (unDefinedClass.startsWith("net.minecraft.") || (unDefinedClass.startsWith("net.minecraftforge.") && !unDefinedClass.startsWith("net.minecraftforge.lex."))) {
						throw new PatchworkInitializationException("Patchwork mod " + initializer.getModId() + " tried to access an unimplemented " + type + ".", t);
					} else {
						throw new PatchworkInitializationException("Patchwork mod " + initializer.getModId() + " tried to access a missing " + type + " from a missing and undeclared, or outdated dependency.", t);
					}
				}

				error.addSuppressed(t);
			}

			ModLoadingContext.get().setActiveContainer(null, "minecraft");

			mods.put(initializer, container);
		}

		if (error != null) {
			throw error;
		}

		ModList.get().setLoadedMods(mods.values());
		// Send initialization events

		dispatch(mods.values(), new RegistryEvent.NewRegistry());
		RegistryEventDispatcher.dispatchRegistryEvents(event -> dispatch(mods.values(), event));
	}

	/**
	 * This is called on the ResourceLoader's thread when a resource loading happens, i.e. during client start-up or F3+T is pressed.
	 * Forge fires the FMLCommonSetupEvent and LifeCycleEvents(FMLClientSetupEvent and FMLDedicatedServerSetupEvent) on its own thread in parallel. Sequence cannot be guaranteed.
	 * IMPORTANT: In Patchwork, we fire all events on the main thread (Client Thread or Server Thread).
	 * @param lifeCycleEvent
	 * @param preSidedRunnable Fired before the LifeCycleEvent, on the main thread. Sequence cannot be guaranteed.
	 * @param postSidedRunnable Fired after the LifeCycleEvent, on the main thread. Sequence cannot be guaranteed.
	 */
	public static void loadMods(Function<ModContainer, Event> lifeCycleEvent, Consumer<Consumer<Supplier<Event>>> preSidedRunnable, Consumer<Consumer<Supplier<Event>>> postSidedRunnable) {
		List<FMLModContainer> mods = ModList.get().applyForEachModContainer(m -> (FMLModContainer) m).collect(Collectors.toList());

		// Loading mod config
		// TODO: Load client and common configs here

		// Mod setup: SETUP
		dispatch(mods, FMLCommonSetupEvent::new);
		// Mod setup: SIDED SETUP
		preSidedRunnable.accept(c -> dispatch(mods, c.get()));
		dispatch(mods, lifeCycleEvent);
		postSidedRunnable.accept(c -> dispatch(mods, c.get()));
		// Mod setup complete
	}

	/**
	 * In Patchwork, we fire all of following events on the main thread (Client Thread or Server Thread).
	 */
	public static void finishMods() {
		List<FMLModContainer> mods = ModList.get().applyForEachModContainer(m -> (FMLModContainer) m).collect(Collectors.toList());

		// Mod setup: ENQUEUE IMC
		dispatch(mods, InterModEnqueueEvent::new);
		// Mod setup: PROCESS IMC
		dispatch(mods, InterModProcessEvent::new);
		// Mod setup: Final completion
		dispatch(mods, FMLLoadCompleteEvent::new);
		// Freezing data, TODO: do we need freezing?
		// GameData.freezeData();
		// NetworkRegistry.lock();
	}

	public static void beginServerModLoading() {
		Object gameInstance = FabricLoader.getInstance().getGameInstance();
		Supplier<DedicatedServer> supplier = () -> (DedicatedServer) gameInstance;

		LOGGER.debug("Patchwork Dedicated Server Mod Loader: Start mod loading.");
		Patchwork.gatherAndInitializeMods();
		Patchwork.loadMods(container -> new FMLDedicatedServerSetupEvent(supplier, container), dummy -> { }, dummy -> { });
	}

	public static void endOfServerModLoading() {
		LOGGER.debug("Patchwork Dedicated Server Mod Loader: Finish mod loading.");
		Patchwork.finishMods();

		LOGGER.debug("Patchwork Dedicated Server Mod Loader: Complete mod loading");
		// Assume there's no error.
		MinecraftForge.EVENT_BUS.start();
	}
}
