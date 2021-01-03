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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.Pair;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.CustomValue;

import net.patchworkmc.api.ModInstance;
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

		List<FMLModContainer> mods = new ArrayList<>();
		List<Pair<String, Supplier<ModInstance>>> modInitializers = new ArrayList<>();
		// Construct forge mods

		// TODO: https://github.com/FabricMC/fabric-loader/pull/313

		for (net.fabricmc.loader.api.ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			String modId = mod.getMetadata().getId();

			CustomValue meta = mod.getMetadata().getCustomValue("patchwork:patcherMeta");

			if (meta != null && meta.getAsObject().get("parent") != null) {
				// synthetic mods are unreliable; don't invoke their entrypoints here
				continue;
			}

			modInitializers.add(new Pair<>(modId, () -> createModInstance(modId)));

			if (meta != null) {
				CustomValue children = meta.getAsObject().get("children");

				if (children != null) {
					for (CustomValue customValue : children.getAsArray()) {
						String childId = customValue.getAsString();
						modInitializers.add(new Pair<>(childId, () -> createModInstance(childId)));
					}
				}
			}
		}

		PatchworkInitializationException error = null;

		for (Pair<String, Supplier<ModInstance>> pair : modInitializers) {
			String modId = pair.getLeft();
			FMLModContainer container = new FMLModContainer(modId);
			boolean loaded = false;

			ModLoadingContext.get().setActiveContainer(container, new FMLJavaModLoadingContext(container));

			try {
				ModInstance instance = pair.getRight().get();

				if (instance != null) {
					container.setMod(instance);
					loaded = true;
				}
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
						throw new PatchworkInitializationException("Patchwork mod " + modId + " tried to access an unimplemented " + type + ".", t);
					} else {
						throw new PatchworkInitializationException("Patchwork mod " + modId + " tried to access a missing " + type + " from a missing and undeclared, or outdated dependency.", t);
					}
				}

				error.addSuppressed(t);
			}

			ModLoadingContext.get().setActiveContainer(null, "minecraft");

			if (loaded) {
				mods.add(container);
			}
		}

		if (error != null) {
			throw error;
		}

		ModList.get().setLoadedMods(mods.stream().map(it -> (ModContainer) it).collect(Collectors.toList()));
		// note: forge fires this per-class when it is registered.
		dispatchEntrypoint(mods, "patchwork:commonAutomaticSubscribers");
		dispatchEntrypoint(mods, "patchwork:" + (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? "client" : "server") + "AutomaticSubscribers");
		// Send initialization events
		//dispatch(mods, new RegistryEvent.NewRegistry());
		dispatchEntrypoint("patchwork:objectHolders");
		dispatchEntrypoint("patchwork:capabilityInject");
		RegistryEventDispatcher.dispatchRegistryEvents(event -> dispatch(mods, event));
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

		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.CLIENT, FabricLoader.getInstance().getConfigDir());
		}

		ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.COMMON, FabricLoader.getInstance().getConfigDir());

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
		Patchwork.finishMods();
		MinecraftForge.EVENT_BUS.start();
	}

	private static ModInstance createModInstance(String modid) {
		List<ModInstance> initializer = FabricLoader.getInstance().getEntrypoints("patchwork:modInstance:" + modid, ModInstance.class);

		if (initializer.size() > 1) {
			throw new AssertionError("Cannot have more than 1 mod_instance for a given modid! aborting!");
		} else if (initializer.size() == 1) {
			return initializer.get(0);
		} else {
			return null;
		}
	}

	private static void dispatchEntrypoint(String name) {
		FabricLoader.getInstance().getEntrypoints(name, ModInitializer.class).forEach(ModInitializer::onInitialize);
	}

	private static void dispatchEntrypoint(Collection<FMLModContainer> mods, String name) {
		HashMap<String, List<ModInitializer>> map = new HashMap<>();
		FabricLoader.getInstance().getEntrypointContainers(name, ModInitializer.class)
				.forEach(container -> map.computeIfAbsent(container.getProvider().getMetadata().getId(),
					id -> new ArrayList<>())
						.add(container.getEntrypoint()));

		for (FMLModContainer mod : mods) {
			List<ModInitializer> inits = map.get(mod.getModId());

			if (inits != null) {
				ModLoadingContext.get().setActiveContainer(mod, new FMLJavaModLoadingContext(mod));
				inits.forEach(ModInitializer::onInitialize);
			}
		}

		ModLoadingContext.get().setActiveContainer(null, "minecraft");
	}
}
