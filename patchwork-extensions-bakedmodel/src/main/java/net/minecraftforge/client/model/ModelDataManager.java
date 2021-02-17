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

package net.minecraftforge.client.model;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Preconditions;
import net.minecraftforge.client.model.data.IModelData;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;

import net.patchworkmc.impl.extensions.bakedmodel.ForgeModelDataProvider;

public class ModelDataManager implements ClientModInitializer {
	private static WeakReference<World> currentWorld = new WeakReference<>(null);

	private static final Map<ChunkPos, Set<BlockPos>> needModelDataRefresh = new ConcurrentHashMap<>();

	private static final Map<ChunkPos, Map<BlockPos, IModelData>> modelDataCache = new ConcurrentHashMap<>();

	private static void cleanCaches(World world) {
		Preconditions.checkNotNull(world, "World must not be null");
		Preconditions.checkArgument(world == MinecraftClient.getInstance().world,
				"Cannot use model data for a world other than the current client world");
		if (world != currentWorld.get()) {
			currentWorld = new WeakReference<>(world);
			needModelDataRefresh.clear();
			modelDataCache.clear();
		}
	}

	public static void requestModelDataRefresh(BlockEntity te) {
		Preconditions.checkNotNull(te, "Tile entity must not be null");
		World world = te.getWorld();

		cleanCaches(world);
		needModelDataRefresh
				.computeIfAbsent(new ChunkPos(te.getPos()), $ -> Collections.synchronizedSet(new HashSet<>()))
				.add(te.getPos());
	}

	private static void refreshModelData(World world, ChunkPos chunk) {
		cleanCaches(world);
		Set<BlockPos> needUpdate = needModelDataRefresh.remove(chunk);

		if (needUpdate != null) {
			Map<BlockPos, IModelData> data = modelDataCache.computeIfAbsent(chunk, $ -> new ConcurrentHashMap<>());

			for (BlockPos pos : needUpdate) {
				BlockEntity toUpdate = world.getBlockEntity(pos);

				if (toUpdate != null && !toUpdate.isRemoved()) {
					data.put(pos, ((ForgeModelDataProvider) toUpdate).getModelData());
				} else {
					data.remove(pos);
				}
			}
		}
	}

	@Override
	public void onInitializeClient() {
		ClientChunkEvents.CHUNK_UNLOAD.register(ModelDataManager::onClientChunkUnload);
	}

	/**
	 * In Forge, this method handles ChunkEvent.Unload event on the FORGE bus(MinecraftForge.EVENT_BUS).
	 */
	public static void onClientChunkUnload(ClientWorld world, WorldChunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		needModelDataRefresh.remove(chunkPos);
		modelDataCache.remove(chunkPos);
	}

	public static @Nullable IModelData getModelData(World world, BlockPos pos) {
		return getModelData(world, new ChunkPos(pos)).get(pos);
	}

	public static Map<BlockPos, IModelData> getModelData(World world, ChunkPos pos) {
		Preconditions.checkArgument(world.isClient, "Cannot request model data for server world");
		refreshModelData(world, pos);
		return modelDataCache.getOrDefault(pos, Collections.emptyMap());
	}
}
