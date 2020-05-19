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

package net.minecraftforge.event.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityCategory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.IWorld;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.biome.Biome.SpawnEntry;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;

/**
 * WorldEvent is fired when an event involving the world occurs.
 *
 * <p>If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.</p>
 *
 * <p>{@link #world} contains the World this event is occurring in.</p>
 *
 * <p>All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.</p>
 */
public class WorldEvent extends Event {
	private final IWorld world;

	public WorldEvent(IWorld world) {
		this.world = world;
	}

	public IWorld getWorld() {
		return world;
	}

	/**
	 * WorldEvent.Load is fired when Minecraft loads a world.
	 *
	 * <p>This event is fired when a world is loaded in
	 * {@link ClientWorld#ClientWorld(ClientPlayNetworkHandler, LevelInfo, DimensionType, int, Profiler, WorldRenderer)},
	 * {@link MinecraftServer#createWorlds(WorldSaveHandler, LevelProperties, LevelInfo, WorldGenerationProgressListener)},
	 * TODO: {@link DimensionManager#initDimension(int)}</p>
	 *
	 * <p>This event is not cancellable.</p>
	 *
	 * <p>This event does not have a result.</p>
	 *
	 * <p>This event is fired on the {@link MinecraftForge#EVENT_BUS}.</p>
	 */
	public static class Load extends WorldEvent {
		public Load(IWorld world) {
			super(world);
		}
	}

	/**
	 * WorldEvent.Unload is fired when Minecraft unloads a world.
	 *
	 * <p>This event is fired when a world is unloaded in
	 * {@link MinecraftClient#joinWorld(ClientWorld)},
	 * {@link MinecraftClient#disconnect()},
	 * {@link MinecraftServer#shutdown()},
	 * TODO: {@link DimensionManager#unloadWorlds()}</p>
	 *
	 * <p>This event is not cancellable.</p>
	 *
	 * <p>This event does not have a result.</p>
	 *
	 * <p>This event is fired on the {@link MinecraftForge#EVENT_BUS}.</p>
	 */
	public static class Unload extends WorldEvent {
		public Unload(IWorld world) {
			super(world);
		}
	}

	/**
	 * WorldEvent.Save is fired when Minecraft saves a world.
	 *
	 * <p>This event is fired when a world is saved in
	 * {@link ServerWorld#save(ProgressListener, boolean, boolean)}.</p>
	 *
	 * <p>This event is not cancellable.</p>
	 *
	 * <p>This event does not have a result.</p>
	 *
	 * <p>This event is fired on the {@link MinecraftForge#EVENT_BUS}.</p>
	 */
	public static class Save extends WorldEvent {
		public Save(IWorld world) {
			super(world);
		}
	}

	/**
	 * Called by {@link ServerWorld} to gather a list of all possible entities that can spawn at the specified location.
	 * If an entry is added to the list, it needs to be a globally unique instance.
	 * The event is called in {@link SpawnHelper#method_8664(ChunkGenerator, EntityCategory, Random, BlockPos)} as well as
	 * {@link SpawnHelper#method_8659(ChunkGenerator, EntityCategory, SpawnEntry, BlockPos)}
	 * where the latter checks for identity, meaning both events must add the same instance.
	 * Canceling the event will result in a empty list, meaning no entity will be spawned.
	 */
	public static class PotentialSpawns extends WorldEvent {
		private final EntityCategory type;
		private final BlockPos pos;
		private final List<SpawnEntry> list;

		public PotentialSpawns(IWorld world, EntityCategory type, BlockPos pos, List<SpawnEntry> oldList) {
			super(world);
			this.pos = pos;
			this.type = type;

			if (oldList != null) {
				this.list = new ArrayList<SpawnEntry>(oldList);
			} else {
				this.list = new ArrayList<SpawnEntry>();
			}
		}

		public EntityCategory getType() {
			return type;
		}

		public BlockPos getPos() {
			return pos;
		}

		public List<SpawnEntry> getList() {
			return list;
		}

		@Override
		public boolean isCancelable() {
			return true;
		}
	}

	/**
	 * Called by ServerWorld when it attempts to create a spawnpoint for a dimension.
	 * Canceling the event will prevent the vanilla code from running.
	 */
	public static class CreateSpawnPosition extends WorldEvent {
		private final LevelInfo settings;

		public CreateSpawnPosition(IWorld world, LevelInfo settings) {
			super(world);
			this.settings = settings;
		}

		public LevelInfo getSettings() {
			return settings;
		}

		@Override
		public boolean isCancelable() {
			return true;
		}
	}
}
