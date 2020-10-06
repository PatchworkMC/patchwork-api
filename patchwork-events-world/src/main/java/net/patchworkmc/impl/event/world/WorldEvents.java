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

package net.patchworkmc.impl.event.world;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.event.world.SaplingGrowTreeEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event;

import net.minecraft.entity.EntityCategory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelInfo;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;

public class WorldEvents implements ModInitializer {
	public static boolean onCreateWorldSpawn(IWorld world, LevelInfo settings) {
		return MinecraftForge.EVENT_BUS.post(new WorldEvent.CreateSpawnPosition(world, settings));
	}

	public static List<Biome.SpawnEntry> getPotentialSpawns(IWorld world, EntityCategory type, BlockPos pos, List<Biome.SpawnEntry> oldSpawns) {
		WorldEvent.PotentialSpawns event = new WorldEvent.PotentialSpawns(world, type, pos, oldSpawns);

		if (MinecraftForge.EVENT_BUS.post(event)) {
			return Collections.emptyList();
		}

		return event.getList();
	}

	public static void onWorldLoad(IWorld world) {
		MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(world));
	}

	public static void onWorldUnload(IWorld world) {
		MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(world));
	}

	public static void onWorldSave(IWorld world) {
		MinecraftForge.EVENT_BUS.post(new WorldEvent.Save(world));
	}

	public static void fireChunkWatch(boolean watch, ServerPlayerEntity entity, ChunkPos chunkpos, ServerWorld world) {
		if (watch) {
			MinecraftForge.EVENT_BUS.post(new ChunkWatchEvent.Watch(entity, chunkpos, world));
		} else {
			MinecraftForge.EVENT_BUS.post(new ChunkWatchEvent.UnWatch(entity, chunkpos, world));
		}
	}

	/**
	 * Called by ChunkEvent.
	 * @return the IWorld instance holding the given chunk, null if not applicable.
	 */
	@Nullable
	public static IWorld getWorldForChunk(Chunk chunk) {
		// replaces IForgeChunk.getWorldForge()
		return chunk instanceof WorldChunk ? ((WorldChunk) chunk).getWorld() : null;
	}

	@Override
	public void onInitialize() {
		ServerWorldEvents.LOAD.register((server, world) -> {
			// Fabric fires this much earlier than Forge does for the overworld
			// So, we're going to manually fire it for the overworld.
			if (world.getDimension().getType() != DimensionType.OVERWORLD) {
				onWorldLoad(world);
			}
		});

		// Fire ChunkEvent.Load on server side, the other location is in MixinThreadedAnvilChunkStorage
		ServerChunkEvents.CHUNK_LOAD.register((server, chunk) -> MinecraftForge.EVENT_BUS.post(new ChunkEvent.Load(chunk)));
		// Fire ChunkEvent.Unload on server side
		ServerChunkEvents.CHUNK_UNLOAD.register((server, chunk) -> MinecraftForge.EVENT_BUS.post(new ChunkEvent.Unload(chunk)));
	}

	public static boolean onSaplingGrowTree(IWorld world, Random rand, BlockPos pos) {
		SaplingGrowTreeEvent event = new SaplingGrowTreeEvent(world, rand, pos);
		MinecraftForge.EVENT_BUS.post(event);
		return event.getResult() != Event.Result.DENY;
	}
}
