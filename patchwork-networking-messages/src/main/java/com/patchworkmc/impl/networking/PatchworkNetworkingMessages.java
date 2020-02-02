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

package com.patchworkmc.impl.networking;

import io.netty.buffer.Unpooled;
import net.minecraftforge.fml.network.FMLPlayMessages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;

public class PatchworkNetworkingMessages implements ModInitializer, MessageFactory {
	private static final Logger LOGGER = LogManager.getLogger("patchwork-networking");
	private static final Identifier PLAY_IDENTIFIER = new Identifier("fml", "play");
	private static final NetworkChannelVersion VERSION = new NetworkChannelVersion("FML2", version -> true, version -> true);
	private static final short SPAWN_ENTITY = 0;

	@Override
	public void onInitialize() {
		PatchworkNetworking.getVersionManager().createChannel(PLAY_IDENTIFIER, VERSION);
		PatchworkNetworking.setFactory(this);

		ClientSidePacketRegistry.INSTANCE.register(PLAY_IDENTIFIER, (context, buf) -> {
			int id = buf.readUnsignedByte();

			if (id == SPAWN_ENTITY) {
				FMLPlayMessages.SpawnEntity spawn = FMLPlayMessages.SpawnEntity.decode(buf);
				FMLPlayMessages.SpawnEntity.handle(spawn, context);
			} else {
				LOGGER.warn("Received an unknown fml:play message with an id of {} and a payload of {} bytes", id, buf.readableBytes());
			}
		});
	}

	@Override
	public Packet<?> getEntitySpawningPacket(Entity entity) {
		FMLPlayMessages.SpawnEntity message = new FMLPlayMessages.SpawnEntity(entity);
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

		buf.writeByte(SPAWN_ENTITY);
		FMLPlayMessages.SpawnEntity.encode(message, buf);

		return ServerSidePacketRegistry.INSTANCE.toPacket(PLAY_IDENTIFIER, buf);
	}
}
