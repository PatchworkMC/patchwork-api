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

package com.patchworkmc.impl.biomes;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biomes.v1.OverworldBiomes;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;

public final class PatchworkBiomes implements ModInitializer {
	private static Set<Biome> failedBiomes = Sets.newHashSet();

	@Override
	public void onInitialize() {
		Registry.BIOME.forEach(PatchworkBiomes::addRivers);
		RegistryEntryAddedCallback.event(Registry.BIOME).register((rawid, id, biome) -> {
			addRivers(biome);
			updateFailedBiomes();
		});
		ServerStartCallback.EVENT.register(server -> updateFailedBiomes());
	}

	private static void addRivers(Biome biome) {
		Biome river = ((RiverSupplier) biome).getRiver();

		if (river == null) {
			failedBiomes.add(biome);
			return;
		}

		setRiverIfNotDefault(biome, river);
	}

	private static void updateFailedBiomes() {
		if (!failedBiomes.isEmpty()) {
			Iterator<Biome> iterator = failedBiomes.iterator();

			while (iterator.hasNext()) {
				Biome biome = iterator.next();
				Biome river = ((RiverSupplier) biome).getRiver();

				if (river == null) {
					continue;
				}

				iterator.remove();

				setRiverIfNotDefault(biome, river);
			}
		}
	}

	private static void setRiverIfNotDefault(Biome biome, Biome river) {
		if (river != getDefaultRiver(biome)) {
			OverworldBiomes.setRiverBiome(biome, river);
		}
	}

	public static Biome getDefaultRiver(Biome biome) {
		if (biome == Biomes.SNOWY_TUNDRA) {
			return Biomes.FROZEN_RIVER;
		} else if (biome == Biomes.MUSHROOM_FIELDS || biome == Biomes.MUSHROOM_FIELD_SHORE) {
			return Biomes.MUSHROOM_FIELD_SHORE;
		}

		return Biomes.RIVER;
	}
}
