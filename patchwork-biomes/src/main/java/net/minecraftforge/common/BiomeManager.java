package net.minecraftforge.common;

import net.fabricmc.fabric.api.biomes.v1.FabricBiomes;
import net.fabricmc.fabric.api.biomes.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biomes.v1.OverworldClimate;
import net.minecraft.util.WeightedPicker;
import net.minecraft.world.biome.Biome;

import java.util.Objects;

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
