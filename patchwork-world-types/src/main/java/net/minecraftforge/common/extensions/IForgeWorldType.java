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

import java.util.function.LongFunction;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.CustomizeBuffetLevelScreen;
import net.minecraft.client.gui.screen.CustomizeFlatLevelScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
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

import net.patchworkmc.mixin.worldtypes.AccessorBiomeLayers;

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
	 *
	 * @param mc  The Minecraft instance
	 * @param gui the createworld GUI
	 */
	@Environment(EnvType.CLIENT)
	default void onCustomizeButton(MinecraftClient mc, CreateWorldScreen gui) {
		if (this == LevelGeneratorType.FLAT) {
			mc.openScreen(new CustomizeFlatLevelScreen(gui, gui.generatorOptionsTag));
		} else if (this == LevelGeneratorType.BUFFET) {
			mc.openScreen(new CustomizeBuffetLevelScreen(gui, gui.generatorOptionsTag));
		}
	}

	default boolean handleSlimeSpawnReduction(java.util.Random random, IWorld world) {
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
	 *
	 * @return The height to render clouds at
	 */
	default float getCloudHeight() {
		return 128.0F;
	}

	default ChunkGenerator<?> createChunkGenerator(World world) {
		return world.dimension.createChunkGenerator();
	}

	/**
	 * Allows modifying the {@link IAreaFactory} used for this type's biome
	 * generation.
	 *
	 * @param <T>            The type of {@link IArea}.
	 * @param <C>            The type of {@link IContextExtended}.
	 * @param parentLayer    The parent layer to feed into any layer you return
	 * @param chunkSettings  The {@link OverworldGenSettings} used to create the
	 *                       {@link GenLayerBiome}.
	 * @param contextFactory A {@link LongFunction} factory to create contexts of
	 *                       the supplied size.
	 * @return An {@link IAreaFactory} that representing the Biomes to be generated.
	 * @see SetBaseBiomesLayer
	 */
	default <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> getBiomeLayer(LayerFactory<T> parentLayer, OverworldChunkGeneratorConfig chunkSettings, LongFunction<C> contextFactory) {
		parentLayer = (new SetBaseBiomesLayer(getWorldType(), chunkSettings)).create(contextFactory.apply(200L), parentLayer);
		parentLayer = AddBambooJungleLayer.INSTANCE.create(contextFactory.apply(1001L), parentLayer);
		parentLayer = AccessorBiomeLayers.stack(1000L, ScaleLayer.NORMAL, parentLayer, 2, contextFactory);
		parentLayer = EaseBiomeEdgeLayer.INSTANCE.create(contextFactory.apply(1000L), parentLayer);
		return parentLayer;
	}
}
