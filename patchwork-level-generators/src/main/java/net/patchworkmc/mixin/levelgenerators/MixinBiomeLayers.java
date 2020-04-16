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

package net.patchworkmc.mixin.levelgenerators;

import java.util.function.LongFunction;

import net.minecraftforge.common.extensions.IForgeWorldType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.biome.layer.AddBambooJungleLayer;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.EaseBiomeEdgeLayer;
import net.minecraft.world.biome.layer.SetBaseBiomesLayer;
import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;

import net.patchworkmc.api.levelgenerators.PatchworkLevelGeneratorType;

@Mixin(BiomeLayers.class)
public class MixinBiomeLayers {
	@Shadow
	private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> stack(long seed, ParentedLayer layer, LayerFactory<T> parent, int count, LongFunction<C> contextProvider) {
		throw new RuntimeException("Failed to shadow BiomeLayers#stack!");
	}

	@Redirect(method = "build(Lnet/minecraft/world/level/LevelGeneratorType;Lnet/minecraft/world/gen/chunk/OverworldChunkGeneratorConfig;Ljava/util/function/LongFunction;)Lcom/google/common/collect/ImmutableList;",
			at = @At(value = "INVOKE", target = "net/minecraft/world/biome/layer/SetBaseBiomesLayer.create(Lnet/minecraft/world/biome/layer/util/LayerSampleContext;Lnet/minecraft/world/biome/layer/util/LayerFactory;)Lnet/minecraft/world/biome/layer/util/LayerFactory;", ordinal = 0))
	private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> addForgeBiomeLayers(SetBaseBiomesLayer instance, C contextParam, LayerFactory<T> parentLayer, LevelGeneratorType generatorType, OverworldChunkGeneratorConfig settings, LongFunction<C> contextProvider) {
		if (generatorType instanceof PatchworkLevelGeneratorType) {
			return ((IForgeWorldType) generatorType).getBiomeLayer(parentLayer, settings, contextProvider);
		} else {
			// vanilla behaviour
			return instance.create(contextParam, parentLayer);
		}
	}

	@Redirect(method = "build(Lnet/minecraft/world/level/LevelGeneratorType;Lnet/minecraft/world/gen/chunk/OverworldChunkGeneratorConfig;Ljava/util/function/LongFunction;)Lcom/google/common/collect/ImmutableList;",
			at = @At(value = "INVOKE", target = "net/minecraft/world/biome/layer/AddBambooJungleLayer.create(Lnet/minecraft/world/biome/layer/util/LayerSampleContext;Lnet/minecraft/world/biome/layer/util/LayerFactory;)Lnet/minecraft/world/biome/layer/util/LayerFactory;", ordinal = 0))
	private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> redirectBambooJungle(AddBambooJungleLayer instance, C contextParam, LayerFactory<T> parentLayer, LevelGeneratorType generatorType, OverworldChunkGeneratorConfig settings, LongFunction<C> contextProvider) {
		if (generatorType instanceof PatchworkLevelGeneratorType) {
			return parentLayer;
		} else {
			// vanilla behaviour
			return instance.create(contextParam, parentLayer);
		}
	}

	@Redirect(method = "build(Lnet/minecraft/world/level/LevelGeneratorType;Lnet/minecraft/world/gen/chunk/OverworldChunkGeneratorConfig;Ljava/util/function/LongFunction;)Lcom/google/common/collect/ImmutableList;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/layer/BiomeLayers;stack(JLnet/minecraft/world/biome/layer/type/ParentedLayer;Lnet/minecraft/world/biome/layer/util/LayerFactory;ILjava/util/function/LongFunction;)Lnet/minecraft/world/biome/layer/util/LayerFactory;", ordinal = 3))
	private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> redirectStack(long seed, ParentedLayer layer, LayerFactory<T> parentLayer, int count, LongFunction<C> contextProvider, LevelGeneratorType generatorType, OverworldChunkGeneratorConfig settings, LongFunction<C> contextProviderParam) {
		if (generatorType instanceof PatchworkLevelGeneratorType) {
			return parentLayer;
		} else {
			// vanilla behaviour
			return stack(seed, layer, parentLayer, count, contextProvider);
		}
	}

	@Redirect(method = "build(Lnet/minecraft/world/level/LevelGeneratorType;Lnet/minecraft/world/gen/chunk/OverworldChunkGeneratorConfig;Ljava/util/function/LongFunction;)Lcom/google/common/collect/ImmutableList;",
			at = @At(value = "INVOKE", target = "net/minecraft/world/biome/layer/EaseBiomeEdgeLayer.create(Lnet/minecraft/world/biome/layer/util/LayerSampleContext;Lnet/minecraft/world/biome/layer/util/LayerFactory;)Lnet/minecraft/world/biome/layer/util/LayerFactory;", ordinal = 0))
	private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> redirectEaseBiomeEdge(EaseBiomeEdgeLayer instance, C contextParam, LayerFactory<T> parentLayer, LevelGeneratorType generatorType, OverworldChunkGeneratorConfig settings, LongFunction<C> contextProvider) {
		if (generatorType instanceof PatchworkLevelGeneratorType) {
			return parentLayer;
		} else {
			// vanilla behaviour
			return instance.create(contextParam, parentLayer);
		}
	}
}
