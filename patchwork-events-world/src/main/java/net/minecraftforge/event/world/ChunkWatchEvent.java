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

import net.minecraftforge.eventbus.api.Event;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;

/**
 * ChunkWatchEvent is fired when an event involving a chunk being watched occurs.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #pos} contains the ChunkPos of the Chunk this event is affecting.<br>
 * {@link #world} contains the World of the Chunk this event is affecting.<br>
 * {@link #player} contains the EntityPlayer that is involved with this chunk being watched. <br>
 * <br>
 * The {@link #player}'s world may not be the same as the world of the chunk
 * when the player is teleporting to another dimension.<br>
 * <br>
 * All children of this event are fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
 */
public class ChunkWatchEvent extends Event {
	private final ServerWorld world;
	private final ServerPlayerEntity player;
	private final ChunkPos pos;

	public ChunkWatchEvent(ServerPlayerEntity player, ChunkPos pos, ServerWorld world) {
		this.player = player;
		this.pos = pos;
		this.world = world;
	}

	public ServerPlayerEntity getPlayer() {
		return this.player;
	}

	public ChunkPos getPos() {
		return this.pos;
	}

	public ServerWorld getWorld() {
		return this.world;
	}

	/**
	 * This event is fired when an EntityPlayer begins watching a chunk.
	 *
	 * <p>This event is fired when a {@link ChunkPos} is added to the watched chunks of an {@link ServerPlayerEntity} in
	 * {@link net.minecraft.server.world.ThreadedAnvilChunkStorage#sendWatchPackets(ServerPlayerEntity, ChunkPos, net.minecraft.network.Packet[], boolean, boolean)}</p>
	 *
	 * <p>This event is not cancellable.</p>
	 *
	 * <p>This event does not have a result.</p>
	 *
	 * <p>This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.</p>
	 */
	public static class Watch extends ChunkWatchEvent {
		public Watch(ServerPlayerEntity player, ChunkPos pos, ServerWorld world) {
			super(player, pos, world);
		}
	}

	/**
	 * This event is fired when a {@link ServerPlayerEntity} stops watching a chunk.
	 *
	 * <p>This event is fired when a {@link ChunkPos} is removed the watched chunks of an {@link ServerPlayerEntity} in
	 * {@link net.minecraft.server.world.ThreadedAnvilChunkStorage#sendWatchPackets(ServerPlayerEntity, ChunkPos, net.minecraft.network.Packet[], boolean, boolean)}</p>
	 *
	 * <p>This event is not cancellable.</p>
	 *
	 * <p>This event does not have a result.</p>
	 *
	 * <p>This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.</p>
	 */
	public static class UnWatch extends ChunkWatchEvent {
		public UnWatch(ServerPlayerEntity player, ChunkPos pos, ServerWorld world) {
			super(player, pos, world);
		}
	}
}
