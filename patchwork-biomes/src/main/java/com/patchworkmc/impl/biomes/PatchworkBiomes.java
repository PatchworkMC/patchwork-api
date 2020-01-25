package com.patchworkmc.impl.biomes;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biomes.v1.OverworldBiomes;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;

public final class PatchworkBiomes implements ModInitializer {
	@Override
	public void onInitialize() {
		Registry.BIOME.forEach(PatchworkBiomes::addRivers);
		RegistryEntryAddedCallback.event(Registry.BIOME).register((rawid, id, biome) -> addRivers(biome));
	}

	private static void addRivers(Biome biome) {
		Biome river = ((ForgeBiomeExt) biome).getRiver();

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
