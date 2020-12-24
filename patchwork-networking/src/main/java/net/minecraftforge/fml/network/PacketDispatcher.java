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

package net.minecraftforge.fml.network;

import java.util.function.BiConsumer;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * Dispatcher for sending packets in response to a received packet. Abstracts out the difference between wrapped packets
 * and unwrapped packets.
 */
public abstract class PacketDispatcher {
	private PacketDispatcher() {
		// No-op
	}

	public abstract void sendPacket(Identifier identifier, PacketByteBuf buffer);

	static class FunctionalDispatcher extends PacketDispatcher {
		private final BiConsumer<Identifier, PacketByteBuf> packetSink;

		FunctionalDispatcher(final BiConsumer<Identifier, PacketByteBuf> packetSink) {
			this.packetSink = packetSink;
		}

		@Override
		public void sendPacket(final Identifier identifier, final PacketByteBuf buffer) {
			packetSink.accept(identifier, buffer);
		}
	}

	static class ClientConnectionDispatcher extends PacketDispatcher {
		private final ClientConnection connection;
		private final int packetIndex;
		private final NetworkDirection direction;

		ClientConnectionDispatcher(ClientConnection connection, int packetIndex, NetworkDirection direction) {
			super();
			this.connection = connection;
			this.packetIndex = packetIndex;
			this.direction = direction;
		}

		@Override
		public void sendPacket(final Identifier identifier, final PacketByteBuf buffer) {
			final ICustomPacket<?> packet = this.direction.buildPacket(Pair.of(buffer, packetIndex), identifier);

			this.connection.send(packet.getThis());
		}
	}
}
