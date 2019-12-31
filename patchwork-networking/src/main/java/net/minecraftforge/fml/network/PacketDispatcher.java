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

	}

	public void sendPacket(Identifier identifier, PacketByteBuf buffer) {
		packetSink.accept(identifier, buffer);
	}

	static class NetworkManagerDispatcher extends PacketDispatcher {
		private final ClientConnection manager;
		private final int packetIndex;
		private final BiFunction<Pair<PacketByteBuf, Integer>, Identifier, ICustomPacket<?>> customPacketSupplier;

		NetworkManagerDispatcher(ClientConnection manager, int packetIndex, BiFunction<Pair<PacketByteBuf, Integer>, Identifier, ICustomPacket<?>> customPacketSupplier) {
			super();
			this.packetSink = this::dispatchPacket;
			this.manager = manager;
			this.packetIndex = packetIndex;
			this.customPacketSupplier = customPacketSupplier;
		}

		private void dispatchPacket(final Identifier resourceLocation, final PacketByteBuf buffer) {
			final ICustomPacket<?> packet = this.customPacketSupplier.apply(Pair.of(buffer, packetIndex), resourceLocation);
			this.manager.send(packet.getThis());
		}
	}
}
