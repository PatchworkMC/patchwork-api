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

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;

public class WorldEventsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		///////////////////////////////////////////////
		/// ChunkEvent.Load and Unload on client side
		///////////////////////////////////////////////
		ClientChunkEvents.CHUNK_LOAD.register((server, chunk) -> MinecraftForge.EVENT_BUS.post(new ChunkEvent.Load(chunk)));
		ClientChunkEvents.CHUNK_UNLOAD.register((server, chunk) -> MinecraftForge.EVENT_BUS.post(new ChunkEvent.Unload(chunk)));
		// Fabric fires the chunk unload event after it's been removed from the client's chunk graph. This probably won't cause any issues.
		/* ClientChunkManager.unload(II)V
		 * if (method_20181(chunk, x, z)) {
		 *     net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Unload(chunk));
		 *     this.chunks.method_20183(i, chunk, (WorldChunk)null);
		 *     ClientChunkEvents.CHUNK_UNLOAD
		 * }
		 */
	}
}
