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

package com.patchworkmc.mixin.event.world;

import java.util.List;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.entity.EntityCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import com.patchworkmc.impl.event.world.IGetWorldFromChunkGenerator;
import com.patchworkmc.impl.event.world.WorldEvents;

@Mixin(SpawnHelper.class)
public class MixinSpawnHelper {
	// Forge adds a parameter to these methods in order to get the world. This... does not seem feasible to do through mixins.
	// So, we extract the world from the ChunkGenerator, courtesy of a interface mixin.
	// For the chosen injection point, we need to shift back 1 so that the change gets applied in time for the isEmpty call.

	@ModifyVariable(method = "method_8664", at = @At(value = "INVOKE", target = "java/util/List.isEmpty ()Z", shift = At.Shift.BEFORE))
	private static List<Biome.SpawnEntry> hookRandomSpawn(List<Biome.SpawnEntry> oldSpawns, ChunkGenerator<?> chunkGenerator, EntityCategory entityCategory, Random random, BlockPos blockPos) {
		IWorld world = ((IGetWorldFromChunkGenerator) chunkGenerator).getWorld();

		return WorldEvents.getPotentialSpawns(world, entityCategory, blockPos, oldSpawns);
	}

	@ModifyVariable(method = "method_8659", at = @At(value = "INVOKE", target = "java/util/List.isEmpty ()Z", shift = At.Shift.BEFORE))
	private static List<Biome.SpawnEntry> hookCanSpawn(List<Biome.SpawnEntry> oldSpawns, ChunkGenerator<?> chunkGenerator, EntityCategory entityCategory, Biome.SpawnEntry spawnEntry, BlockPos blockPos) {
		IWorld world = ((IGetWorldFromChunkGenerator) chunkGenerator).getWorld();

		return WorldEvents.getPotentialSpawns(world, entityCategory, blockPos, oldSpawns);
	}
}
