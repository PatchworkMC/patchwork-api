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

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectArrayMap;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.IndexedMessageCodec.MessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class IndexedMessageCodec {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Marker SIMPLENET = MarkerManager.getMarker("SIMPLENET");
	private final Short2ObjectArrayMap<MessageHandler<?>> indices = new Short2ObjectArrayMap<>();
	private final Object2ObjectArrayMap<Class<?>, MessageHandler<?>> types = new Object2ObjectArrayMap<>();
	private final String channelName;

	public IndexedMessageCodec() {
		this.channelName = "MISSING CHANNEL";
	}

	IndexedMessageCodec(final Identifier channelName) {
		this.channelName = channelName.toString();
	}

	private static <M> void tryDecode(PacketByteBuf payload, NetworkEvent.Context context, int payloadIndex, MessageHandler<M> codec) {
		if (codec.decoder == null) {
			return;
		}

		Function<PacketByteBuf, M> decoder = codec.decoder;
		M message = decoder.apply(payload);

		if (payloadIndex != Integer.MIN_VALUE && codec.getLoginIndexSetter() != null) {
			codec.getLoginIndexSetter().accept(message, payloadIndex);
		}

		codec.messageConsumer.accept(message, () -> context);
	}

	private static <M> int tryEncode(PacketByteBuf target, M message, MessageHandler<M> codec) {
		if (codec.encoder != null) {
			target.writeByte(codec.index & 0xff);
			codec.encoder.accept(message, target);
		}

		if (codec.loginIndexGetter != null) {
			return codec.loginIndexGetter.apply(message);
		} else {
			return Integer.MIN_VALUE;
		}
	}

	@SuppressWarnings("unchecked")
	public <M> MessageHandler<M> findMessageType(final M message) {
		return (MessageHandler<M>) types.get(message.getClass());
	}

	@SuppressWarnings("unchecked")
	<M> MessageHandler<M> findIndex(final short index) {
		return (MessageHandler<M>) indices.get(index);
	}

	public <M> int build(M message, PacketByteBuf target) {
		MessageHandler<M> codec = findMessageType(message);

		if (codec == null) {
			LOGGER.error(SIMPLENET, "Received invalid message {} on channel {}", message.getClass().getName(), channelName);
			throw new IllegalArgumentException("Invalid message " + message.getClass().getName());
		}

		return tryEncode(target, message, codec);
	}

	void consume(PacketByteBuf payload, int payloadIndex, NetworkEvent.Context context) {
		if (payload == null) {
			LOGGER.error(SIMPLENET, "Received empty payload on channel {}", channelName);
			return;
		}

		short discriminator = payload.readUnsignedByte();
		final MessageHandler<?> messageHandler = indices.get(discriminator);

		if (messageHandler == null) {
			LOGGER.error(SIMPLENET, "Received invalid discriminator byte {} on channel {}", discriminator, channelName);
			return;
		}

		tryDecode(payload, context, payloadIndex, messageHandler);
	}

	<M> MessageHandler<M> addCodecIndex(int index, Class<M> messageType, BiConsumer<M, PacketByteBuf> encoder, Function<PacketByteBuf, M> decoder, BiConsumer<M, Supplier<NetworkEvent.Context>> messageConsumer) {
		return new MessageHandler<>(index, messageType, encoder, decoder, messageConsumer);
	}

	class MessageHandler<M> {
		private final BiConsumer<M, PacketByteBuf> encoder;
		private final Function<PacketByteBuf, M> decoder;
		private final int index;
		private final BiConsumer<M, Supplier<NetworkEvent.Context>> messageConsumer;
		private final Class<M> messageType;
		@Nullable
		private BiConsumer<M, Integer> loginIndexSetter;
		@Nullable
		private Function<M, Integer> loginIndexGetter;

		MessageHandler(int index, Class<M> messageType, BiConsumer<M, PacketByteBuf> encoder, Function<PacketByteBuf, M> decoder, BiConsumer<M, Supplier<NetworkEvent.Context>> messageConsumer) {
			this.index = index;
			this.messageType = messageType;
			this.encoder = encoder;
			this.decoder = decoder;
			this.messageConsumer = messageConsumer;
			this.loginIndexGetter = null;
			this.loginIndexSetter = null;
			indices.put((short) (index & 0xff), this);
			types.put(messageType, this);
		}

		@Nullable
		BiConsumer<M, Integer> getLoginIndexSetter() {
			return this.loginIndexSetter;
		}

		void setLoginIndexSetter(BiConsumer<M, Integer> loginIndexSetter) {
			this.loginIndexSetter = loginIndexSetter;
		}

		// Leaving this alone for now because it's a public method.
		public Optional<Function<M, Integer>> getLoginIndexGetter() {
			return Optional.ofNullable(this.loginIndexGetter);
		}

		void setLoginIndexGetter(Function<M, Integer> loginIndexGetter) {
			this.loginIndexGetter = loginIndexGetter;
		}

		M newInstance() {
			try {
				return messageType.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				LOGGER.error("Invalid login message", e);
				throw new RuntimeException(e);
			}
		}
	}
}
