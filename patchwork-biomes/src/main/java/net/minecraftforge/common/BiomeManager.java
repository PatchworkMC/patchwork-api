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

package net.minecraftforge.common;

import java.util.Objects;

import net.minecraft.util.WeightedPicker;
import net.minecraft.world.biome.Biome;

import net.fabricmc.fabric.api.biomes.v1.FabricBiomes;
import net.fabricmc.fabric.api.biomes.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biomes.v1.OverworldClimate;

public class BiomeManager {
	public static void addSpawnBiome(Biome biome) {
		FabricBiomes.addSpawnBiome(biome);
	}

	public static void addBiome(BiomeType type, BiomeEntry entry) {
		Objects.requireNonNull(type, "type must not be null");
		Objects.requireNonNull(entry, "entry must not be null");

		OverworldClimate climate = type.getClimate();

		OverworldBiomes.addContinentalBiome(entry.biome, climate, entry.getWeight() / 10.0);
	}

	public enum BiomeType {
		DESERT, WARM, COOL, ICY;

		private OverworldClimate getClimate() {
			switch (this) {
			case DESERT:
				return OverworldClimate.DRY;
			case WARM:
				return OverworldClimate.TEMPERATE;
			case COOL:
				return OverworldClimate.COOL;
			case ICY:
				return OverworldClimate.SNOWY;
			default:
				throw new IllegalStateException("Someone's been tampering with the BiomeType enum!");
			}
		}
	}

	public static class BiomeEntry extends WeightedPicker.Entry {
		public final Biome biome;

		public BiomeEntry(Biome biome, int weight) {
			super(weight);

			this.biome = biome;
		}

		private int getWeight() {
			return this.weight;
		}
	}
}
