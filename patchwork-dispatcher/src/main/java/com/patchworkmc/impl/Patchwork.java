/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2019, 2019
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.registries.ForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import com.patchworkmc.api.ForgeInitializer;
import com.patchworkmc.impl.registries.RegistryClassMapping;

public class Patchwork implements ModInitializer {
	private static final Logger LOGGER = LogManager.getLogger(Patchwork.class);

	private static void dispatchRegistryEvents(Map<ForgeInitializer, FMLModContainer> mods) {
		// verify supers

		List<Identifier> registries = new ArrayList<>(Registry.REGISTRIES.getIds());

		registries.remove(Registry.REGISTRIES.getId(Registry.BLOCK));
		registries.remove(Registry.REGISTRIES.getId(Registry.ITEM));

		registries.sort((o1, o2) -> String.valueOf(o1).compareToIgnoreCase(String.valueOf(o2)));

		registries.add(0, Registry.REGISTRIES.getId(Registry.BLOCK));
		registries.add(1, Registry.REGISTRIES.getId(Registry.ITEM));

		LOGGER.info("Dispatching registry events for: " + registries);

		for (Identifier identifier : registries) {
			Registry registry = Registry.REGISTRIES.get(identifier);
			Class superType = RegistryClassMapping.getClass(identifier);

			ForgeRegistry forgeRegistry = new ForgeRegistry(identifier, registry, superType);

			// Note: this checks to see if supers is correct
			/*for(Map.Entry<Identifier, Object> entry: (Set<Map.Entry<Identifier, Object>>)forgeRegistry.getEntries()) {
				if(!superType.isAssignableFrom(entry.getValue().getClass())) {
					System.err.println("Bad registry type for " + identifier + " (" + entry.getKey() + ")");
					throw new RuntimeException();
				}
			}*/

			dispatch(mods, new RegistryEvent.Register(forgeRegistry));
		}
	}

	private static void dispatch(Map<ForgeInitializer, FMLModContainer> mods, Event event) {
		for (Map.Entry<ForgeInitializer, FMLModContainer> entry : mods.entrySet()) {
			ForgeInitializer initializer = entry.getKey();
			FMLModContainer container = entry.getValue();

			ModLoadingContext.get().setActiveContainer(new ModContainer(initializer.getModId()), new FMLJavaModLoadingContext(container));

			container.getEventBus().post(event);

			ModLoadingContext.get().setActiveContainer(null, "minecraft");
		}
	}

	@Override
	public void onInitialize() {
		Map<ForgeInitializer, FMLModContainer> mods = new HashMap<>();
		List<String> modIds = new ArrayList<>();
		// Construct forge mods

		for (ForgeInitializer initializer : FabricLoader.getInstance().getEntrypoints("patchwork", ForgeInitializer.class)) {
			LOGGER.info("Constructing Forge mod: " + initializer);

			FMLModContainer container = new FMLModContainer();
			ModLoadingContext.get().setActiveContainer(new ModContainer(initializer.getModId()), new FMLJavaModLoadingContext(container));

			initializer.onForgeInitialize();

			ModLoadingContext.get().setActiveContainer(null, "minecraft");

			mods.put(initializer, container);
			modIds.add(initializer.getModId());
		}

		// Init ModList
		ModList.create(modIds);
		// Send initialization events

		dispatchRegistryEvents(mods);
		// TODO: One per modcontainer
		dispatch(mods, new FMLCommonSetupEvent(new ModContainer("minecraft")));
		dispatch(mods, new InterModEnqueueEvent(new ModContainer("minecraft")));
		dispatch(mods, new InterModProcessEvent(new ModContainer("minecraft")));
		dispatch(mods, new FMLLoadCompleteEvent(new ModContainer("minecraft")));

		MinecraftForge.EVENT_BUS.start();
	}
}
