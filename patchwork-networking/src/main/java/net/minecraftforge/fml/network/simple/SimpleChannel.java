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

package net.minecraftforge.fml.network.simple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import net.minecraftforge.fml.network.ICustomPacket;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.patchworkmc.impl.networking.ListenableChannel;

public class SimpleChannel implements ListenableChannel {
	private final Identifier channelName;
	private final IndexedMessageCodec indexedCodec;
	private List<Function<Boolean, List<Pair<String, ?>>>> loginPackets;

	public SimpleChannel(Identifier name) {
		this.channelName = name;
		this.indexedCodec = new IndexedMessageCodec(channelName);
		this.loginPackets = new ArrayList<>();
	}

	@Override
	public void onPacket(ICustomPacket<?> packet, NetworkEvent.Context context) {
		this.indexedCodec.consume(packet.getInternalData(), packet.getIndex(), context);
	}

	@Override
	public void onRegistrationChange(NetworkEvent.ChannelRegistrationChangeEvent event) {
		// No-op
	}

	@Override
	public void onGatherLoginPayloads(List<NetworkRegistry.LoginPayload> payloads, boolean isLocal) {
		for (Function<Boolean, List<Pair<String, ?>>> packetGenerator: loginPackets) {
			List<Pair<String, ?>> packets = packetGenerator.apply(isLocal);

			for (Pair<String, ?> pair: packets) {
				PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());

				this.indexedCodec.build(pair.getRight(), buffer);

				payloads.add(new NetworkRegistry.LoginPayload(buffer, this.channelName, pair.getLeft()));
			}
		}
	}

	public <M> int encodeMessage(M message, final PacketByteBuf target) {
		return this.indexedCodec.build(message, target);
	}

	public <M> IndexedMessageCodec.MessageHandler<M> registerMessage(int index, Class<M> messageType, BiConsumer<M, PacketByteBuf> encoder, Function<PacketByteBuf, M> decoder, BiConsumer<M, Supplier<NetworkEvent.Context>> messageConsumer) {
		return this.indexedCodec.addCodecIndex(index, messageType, encoder, decoder, messageConsumer);
	}

	private <M> Pair<PacketByteBuf, Integer> toBuffer(M message) {
		final PacketByteBuf bufIn = new PacketByteBuf(Unpooled.buffer());
		int index = encodeMessage(message, bufIn);
		return Pair.of(bufIn, index);
	}

	public <M> void sendToServer(M message) {
		sendTo(message, MinecraftClient.getInstance().getNetworkHandler().getConnection(), NetworkDirection.PLAY_TO_SERVER);
	}

	public <M> void sendTo(M message, ClientConnection connection, NetworkDirection direction) {
		connection.send(toVanillaPacket(message, direction));
	}

	/**
	 * Send a message to the {@link PacketDistributor.PacketTarget} from a {@link PacketDistributor} instance.
	 *
	 * <pre>
	 *     channel.send(PacketDistributor.PLAYER.with(()->player), message)
	 * </pre>
	 *
	 * @param target The curried target from a PacketDistributor
	 * @param message The message to send
	 * @param <M> The type of the message
	 */
	public <M> void send(PacketDistributor.PacketTarget target, M message) {
		target.send(toVanillaPacket(message, target.getDirection()));
	}

	public <M> Packet<?> toVanillaPacket(M message, NetworkDirection direction) {
		return direction.buildPacket(toBuffer(message), channelName).getThis();
	}

	public <M> void reply(M msgToReply, NetworkEvent.Context context) {
		context.getPacketDispatcher().sendPacket(channelName, toBuffer(msgToReply).getLeft());
	}

	/**
	 * Build a new MessageBuilder. The type should implement {@link java.util.function.IntSupplier} if it is a login
	 * packet.
	 * @param type Type of message
	 * @param id id in the indexed codec
	 * @param <M> Type of type
	 * @return a MessageBuilder
	 */
	public <M> MessageBuilder<M> messageBuilder(final Class<M> type, int id) {
		return MessageBuilder.forType(this, type, id);
	}

	public static class MessageBuilder<M> {
		private SimpleChannel channel;
		private Class<M> type;
		private int id;
		private BiConsumer<M, PacketByteBuf> encoder;
		private Function<PacketByteBuf, M> decoder;
		private BiConsumer<M, Supplier<NetworkEvent.Context>> consumer;
		private Function<M, Integer> loginIndexGetter;
		private BiConsumer<M, Integer> loginIndexSetter;
		private Function<Boolean, List<Pair<String, M>>> loginPacketGenerators;

		private static <M> MessageBuilder<M> forType(final SimpleChannel channel, final Class<M> type, int id) {
			MessageBuilder<M> builder = new MessageBuilder<>();
			builder.channel = channel;
			builder.id = id;
			builder.type = type;
			return builder;
		}

		public MessageBuilder<M> encoder(BiConsumer<M, PacketByteBuf> encoder) {
			this.encoder = encoder;
			return this;
		}

		public MessageBuilder<M> decoder(Function<PacketByteBuf, M> decoder) {
			this.decoder = decoder;
			return this;
		}

		public MessageBuilder<M> loginIndex(Function<M, Integer> loginIndexGetter, BiConsumer<M, Integer> loginIndexSetter) {
			this.loginIndexGetter = loginIndexGetter;
			this.loginIndexSetter = loginIndexSetter;
			return this;
		}

		public MessageBuilder<M> buildLoginPacketList(Function<Boolean, List<Pair<String, M>>> loginPacketGenerators) {
			this.loginPacketGenerators = loginPacketGenerators;
			return this;
		}

		public MessageBuilder<M> markAsLoginPacket() {
			this.loginPacketGenerators = (isLocal) -> {
				try {
					return Collections.singletonList(Pair.of(type.getName(), type.newInstance()));
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException("Inaccessible no-arg constructor for message " + type.getName(), e);
				}
			};
			return this;
		}

		public MessageBuilder<M> consumer(BiConsumer<M, Supplier<NetworkEvent.Context>> consumer) {
			this.consumer = consumer;
			return this;
		}

		/**
		 * Function returning a boolean "packet handled" indication, for simpler channel building.
		 * @param handler a handler
		 * @return this
		 */
		public MessageBuilder<M> consumer(ToBooleanBiFunction<M, Supplier<NetworkEvent.Context>> handler) {
			this.consumer = (message, context) -> {
				boolean handled = handler.applyAsBool(message, context);
				context.get().setPacketHandled(handled);
			};
			return this;
		}

		public void add() {
			final IndexedMessageCodec.MessageHandler<M> message = this.channel.registerMessage(this.id, this.type, this.encoder, this.decoder, this.consumer);

			if (this.loginIndexSetter != null) {
				message.setLoginIndexSetter(this.loginIndexSetter);
			}

			if (this.loginIndexGetter != null) {
				if (!IntSupplier.class.isAssignableFrom(this.type)) {
					throw new IllegalArgumentException("Login packet type that does not supply an index as an IntSupplier");
				}

				message.setLoginIndexGetter(this.loginIndexGetter);
			}

			if (this.loginPacketGenerators != null) {
				this.channel.loginPackets.add((Function<Boolean, List<Pair<String, ?>>>) (Object) this.loginPacketGenerators);

				// TODO: Login packet stuff
				throw new UnsupportedOperationException("Login packet generators are unsupported");
			}
		}

		public interface ToBooleanBiFunction<T, U> {
			boolean applyAsBool(T first, U second);
		}
	}
}
