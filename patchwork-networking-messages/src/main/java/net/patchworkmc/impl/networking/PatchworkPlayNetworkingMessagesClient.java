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

package net.patchworkmc.impl.networking;

import net.minecraftforge.fml.network.FMLPlayMessages;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;

public class PatchworkPlayNetworkingMessagesClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// TODO: Move to client initializer
		ClientSidePacketRegistry.INSTANCE.register(PatchworkPlayNetworkingMessages.IDENTIFIER, (context, buf) -> {
			int id = buf.readUnsignedByte();

			if (id == PatchworkPlayNetworkingMessages.SPAWN_ENTITY) {
				FMLPlayMessages.SpawnEntity spawn = FMLPlayMessages.SpawnEntity.decode(buf);
				FMLPlayMessages.SpawnEntity.handle(spawn, context);
			} else if (id == PatchworkPlayNetworkingMessages.OPEN_CONTAINER) {
				FMLPlayMessages.OpenContainer open = FMLPlayMessages.OpenContainer.decode(buf);
				FMLPlayMessages.OpenContainer.handle(open, context);
			} else {
				PatchworkPlayNetworkingMessages.LOGGER.warn("Received an unknown fml:play message with an id of {} and a payload of {} bytes", id, buf.readableBytes());
			}
		});
	}
}
