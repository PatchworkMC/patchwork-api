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

import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.Chunk;

import net.patchworkmc.impl.event.world.WorldEvents;

/**
 * ChunkEvent is fired when an event involving a chunk occurs.<br>
 * If a method utilizes this {@link net.minecraftforge.eventbus.api.Event} as
 * its parameter, the method will receive every child event of this class.<br>
 * <br>
 * {@link #Chunk} contains the Chunk this event is affecting.<br>
 * <br>
 * All children of this event are fired on the
 * {@link MinecraftForge#EVENT_BUS}.<br>
 */
public class ChunkEvent extends WorldEvent {
	private final Chunk chunk;

	public ChunkEvent(Chunk chunk) {
		super(WorldEvents.getWorldForChunk(chunk));
		this.chunk = chunk;
	}

	public ChunkEvent(Chunk chunk, IWorld world) {
		super(world);
		this.chunk = chunk;
	}

	public Chunk getChunk() {
		return chunk;
	}

	/**
	 * ChunkEvent.Load is fired when vanilla Minecraft attempts to load a Chunk into
	 * the world.<br>
	 * This event is fired during chunk loading in <br>
	 * {@link ChunkProviderClient#loadChunk(int, int)}, <br>
	 * Chunk.onChunkLoad(). <br>
	 * <br>
	 * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
	 * <br>
	 * This event does not have a result. {@link HasResult} <br>
	 * <br>
	 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
	 */
	public static class Load extends ChunkEvent {
		public Load(Chunk chunk) {
			super(chunk);
		}
	}

	/**
	 * ChunkEvent.Unload is fired when vanilla Minecraft attempts to unload a Chunk
	 * from the world.<br>
	 * This event is fired during chunk unloading in <br>
	 * Chunk.onChunkUnload(). <br>
	 * <br>
	 * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
	 * <br>
	 * This event does not have a result. {@link HasResult} <br>
	 * <br>
	 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
	 */
	public static class Unload extends ChunkEvent {
		public Unload(Chunk chunk) {
			super(chunk);
		}
	}
}

