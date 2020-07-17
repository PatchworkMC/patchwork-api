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

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityCategory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.LevelInfo;

import net.patchworkmc.impl.event.world.WorldEvents;

/*
 * Note: this class is intended for mod use only, to dispatch to the implementations kept in their own modules.
 * Do not keep implementation details here, methods should be thin wrappers around methods in other modules.
 */
public class ForgeHooks {
	public static boolean onCreateWorldSpawn(World world, LevelInfo settings) {
		return WorldEvents.onCreateWorldSpawn(world, settings);
	}

	@Nullable
	public static List<Biome.SpawnEntry> getPotentialSpawns(IWorld world, EntityCategory type, BlockPos pos, List<Biome.SpawnEntry> oldList) {
		return WorldEvents.getPotentialSpawns(world, type, pos, oldList);
	}

	public static void onDifficultyChange(Difficulty difficulty, Difficulty oldDifficulty) {
		WorldEvents.onDifficultyChange(difficulty, oldDifficulty);
	}

	public static void fireChunkWatch(boolean watch, ServerPlayerEntity entity, ChunkPos chunkpos, ServerWorld world) {
		WorldEvents.fireChunkWatch(watch, entity, chunkpos, world);
	}
}
