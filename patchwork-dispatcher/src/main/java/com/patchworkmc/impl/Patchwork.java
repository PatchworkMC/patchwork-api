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

package com.patchworkmc.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.dedicated.DedicatedServer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import com.patchworkmc.api.ForgeInitializer;
import com.patchworkmc.impl.registries.RegistryEventDispatcher;

public class Patchwork implements ModInitializer {
	private static final Logger LOGGER = LogManager.getLogger(Patchwork.class);

	private static void dispatch(Map<ForgeInitializer, FMLModContainer> mods, Event event) {
		dispatch(mods, container -> event);
	}

	private static void dispatch(Map<ForgeInitializer, FMLModContainer> mods, Function<ModContainer, Event> provider) {
		for (FMLModContainer container : mods.values()) {
			ModLoadingContext.get().setActiveContainer(container, new FMLJavaModLoadingContext(container));

			container.getEventBus().post(provider.apply(container));

			ModLoadingContext.get().setActiveContainer(null, "minecraft");
		}
	}

	@Override
	public void onInitialize() {
		ForgeRegistries.init();

		Map<ForgeInitializer, FMLModContainer> mods = new HashMap<>();

		// Construct forge mods

		for (ForgeInitializer initializer : FabricLoader.getInstance().getEntrypoints("patchwork", ForgeInitializer.class)) {
			LOGGER.info("Constructing Forge mod: " + initializer.getModId());

			FMLModContainer container = new FMLModContainer(initializer.getModId());
			ModLoadingContext.get().setActiveContainer(container, new FMLJavaModLoadingContext(container));

			initializer.onForgeInitialize();

			ModLoadingContext.get().setActiveContainer(null, "minecraft");

			mods.put(initializer, container);
		}

		// Send initialization events

		RegistryEventDispatcher.dispatchRegistryEvents(event -> dispatch(mods, event));
		dispatch(mods, FMLCommonSetupEvent::new);

		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			dispatch(mods, container -> new FMLClientSetupEvent(MinecraftClient::getInstance, container));
		});

		DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
			Object gameInstance = FabricLoader.getInstance().getGameInstance();
			Supplier<DedicatedServer> supplier = () -> (DedicatedServer) gameInstance;

			dispatch(mods, container -> new FMLDedicatedServerSetupEvent(supplier, container));
		});

		dispatch(mods, InterModEnqueueEvent::new);
		dispatch(mods, InterModProcessEvent::new);
		dispatch(mods, FMLLoadCompleteEvent::new);

		MinecraftForge.EVENT_BUS.start();
	}
}
