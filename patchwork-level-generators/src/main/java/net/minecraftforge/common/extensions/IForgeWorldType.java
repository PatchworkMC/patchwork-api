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

package net.minecraftforge.common.extensions;

import java.util.Random;
import java.util.function.LongFunction;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.CustomizeBuffetLevelScreen;
import net.minecraft.client.gui.screen.CustomizeFlatLevelScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.layer.AddBambooJungleLayer;
import net.minecraft.world.biome.layer.EaseBiomeEdgeLayer;
import net.minecraft.world.biome.layer.ScaleLayer;
import net.minecraft.world.biome.layer.SetBaseBiomesLayer;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.patchworkmc.mixin.levelgenerators.AccessorBiomeLayers;

public interface IForgeWorldType {
	default LevelGeneratorType getWorldType() {
		return (LevelGeneratorType) this;
	}

	/**
	 * Called when 'Create New World' button is pressed before starting game.
	 */
	default void onGUICreateWorldPress() {
	}

	/**
	 * Called when the 'Customize' button is pressed on world creation GUI.
	 */
	@Environment(EnvType.CLIENT)
	default void onCustomizeButton(MinecraftClient client, CreateWorldScreen screen) {
		if (this == LevelGeneratorType.FLAT) {
			client.openScreen(new CustomizeFlatLevelScreen(screen, screen.generatorOptionsTag));
		} else if (this == LevelGeneratorType.BUFFET) {
			client.openScreen(new CustomizeBuffetLevelScreen(screen, screen.generatorOptionsTag));
		}
	}

	default boolean handleSlimeSpawnReduction(Random random, WorldAccess world) {
		return this == LevelGeneratorType.FLAT && random.nextInt(4) != 1;
	}

	default double getHorizon(World world) {
		return this == LevelGeneratorType.FLAT ? 0.0D : 63.0D;
	}

	default double voidFadeMagnitude() {
		return this == LevelGeneratorType.FLAT ? 1.0D : 0.03125D;
	}

	/**
	 * Get the height to render the clouds for this world type.
	 */
	default float getCloudHeight() {
		return 128.0F;
	}

	default ChunkGenerator<?> createChunkGenerator(World world) {
		return world.dimension.createChunkGenerator();
	}

	/**
	 * Allows modifying the {@link LayerFactory} used for this type's biome
	 * generation.
	 *
	 * @param <T>            The type of {@link LayerSampler}.
	 * @param <C>            The type of {@link LayerSampleContext}.
	 * @param parentLayer    The parent layer to feed into any layer you return
	 * @param settings  The {@link OverworldChunkGeneratorConfig} used to create the
	 *                       {@link SetBaseBiomesLayer}.
	 * @param contextFactory A {@link LongFunction} factory to create contexts of
	 *                       the supplied size.
	 * @return A {@link LayerFactory} that representing the Biomes to be generated.
	 * @see SetBaseBiomesLayer
	 */
	default <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> getBiomeLayer(LayerFactory<T> parentLayer, OverworldChunkGeneratorConfig settings, LongFunction<C> contextFactory) {
		parentLayer = (new SetBaseBiomesLayer(getWorldType(), settings)).create(contextFactory.apply(200L), parentLayer);
		parentLayer = AddBambooJungleLayer.INSTANCE.create(contextFactory.apply(1001L), parentLayer);
		parentLayer = AccessorBiomeLayers.patchwork$stack(1000L, ScaleLayer.NORMAL, parentLayer, 2, contextFactory);
		parentLayer = EaseBiomeEdgeLayer.INSTANCE.create(contextFactory.apply(1000L), parentLayer);
		return parentLayer;
	}
}
