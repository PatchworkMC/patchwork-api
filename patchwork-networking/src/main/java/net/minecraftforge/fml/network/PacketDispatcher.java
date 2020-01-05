/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2019, 2019
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
import java.util.function.BiFunction;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.network.ClientConnection;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

/**
 * Dispatcher for sending packets in response to a received packet. Abstracts out the difference between wrapped packets
 * and unwrapped packets.
 */
public class PacketDispatcher {
	BiConsumer<Identifier, PacketByteBuf> packetSink;

	PacketDispatcher(final BiConsumer<Identifier, PacketByteBuf> packetSink) {
		this.packetSink = packetSink;
	}

	private PacketDispatcher() {
		// NO-OP; cannot call package-private constructor due to Java limitations
	}

	public void sendPacket(Identifier identifier, PacketByteBuf buffer) {
		packetSink.accept(identifier, buffer);
	}

	static class ClientConnectionDispatcher extends PacketDispatcher {
		private final ClientConnection connection;
		private final int packetIndex;
		private final BiFunction<Pair<PacketByteBuf, Integer>, Identifier, ICustomPacket<?>> customPacketSupplier;

		ClientConnectionDispatcher(ClientConnection connection, int packetIndex, BiFunction<Pair<PacketByteBuf, Integer>, Identifier, ICustomPacket<?>> customPacketSupplier) {
			super();
			this.connection = connection;
			this.packetIndex = packetIndex;
			this.customPacketSupplier = customPacketSupplier;
		}

		private void dispatchPacket(final Identifier connection, final PacketByteBuf buffer) {
			final ICustomPacket<?> packet = this.customPacketSupplier.apply(Pair.of(buffer, packetIndex), connection);
			this.connection.send(packet.getThis());
		}
	}
}
