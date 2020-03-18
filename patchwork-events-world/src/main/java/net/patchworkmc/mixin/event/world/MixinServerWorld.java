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

package net.patchworkmc.mixin.event.world;

import java.util.function.BiFunction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;

import net.patchworkmc.impl.event.world.WorldEvents;

@Mixin(ServerWorld.class)
public abstract class MixinServerWorld extends World {
	protected MixinServerWorld(LevelProperties levelProperties, DimensionType dimensionType, BiFunction<World, Dimension, ChunkManager> chunkManagerProvider, Profiler profiler, boolean isClient) {
		super(levelProperties, dimensionType, chunkManagerProvider, profiler, isClient);
	}

	@Inject(method = "save", at = @At(value = "INVOKE", target = "net/minecraft/server/world/ServerChunkManager.save (Z)V"))
	private void hookSave(CallbackInfo info) {
		WorldEvents.onWorldSave((ServerWorld) (Object) this);
	}

	// TODO: consider adding a shift to before obtaining the ChunkManager to match forge more closely
	// I don't think it'll make much of a difference.
	@Inject(method = "init", cancellable = true, at = @At(value = "INVOKE", target = "net/minecraft/world/gen/chunk/ChunkGenerator.getBiomeSource ()Lnet/minecraft/world/biome/source/BiomeSource;"))
	private void hookInitForCreateWorldSpawn(LevelInfo levelInfo, CallbackInfo info) {
		ServerWorld world = (ServerWorld) (Object) this;

		if (WorldEvents.onCreateWorldSpawn(world, levelInfo)) {
			info.cancel();
		}
	}
}
