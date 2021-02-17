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

import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraftforge.fml.LogicalSide;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import net.patchworkmc.mixin.networking.accessor.CustomPayloadC2SPacketAccessor;
import net.patchworkmc.mixin.networking.accessor.CustomPayloadS2CPacketAccessor;

public enum NetworkDirection {
	PLAY_TO_SERVER(NetworkEvent.ClientCustomPayloadEvent::new, LogicalSide.CLIENT, 1),
	PLAY_TO_CLIENT(NetworkEvent.ServerCustomPayloadEvent::new, LogicalSide.SERVER, 0),
	LOGIN_TO_SERVER(NetworkEvent.ClientCustomPayloadLoginEvent::new, LogicalSide.CLIENT, 3),
	LOGIN_TO_CLIENT(NetworkEvent.ServerCustomPayloadLoginEvent::new, LogicalSide.SERVER, 2);

	private final BiFunction<ICustomPacket<?>, Supplier<NetworkEvent.Context>, NetworkEvent> eventSupplier;
	private final LogicalSide logicalSide;
	private final int otherWay;

	NetworkDirection(BiFunction<ICustomPacket<?>, Supplier<NetworkEvent.Context>, NetworkEvent> supplier, LogicalSide side, int i) {
		this.eventSupplier = supplier;
		this.logicalSide = side;
		this.otherWay = i;
	}

	@NotNull
	public NetworkDirection reply() {
		return NetworkDirection.values()[this.otherWay];
	}

	public NetworkEvent getEvent(final ICustomPacket<?> buffer, final Supplier<NetworkEvent.Context> context) {
		return this.eventSupplier.apply(buffer, context);
	}

	@NotNull
	public LogicalSide getOriginationSide() {
		return logicalSide;
	}

	@NotNull
	public LogicalSide getReceptionSide() {
		return reply().logicalSide;
	}

	@Nullable
	public static NetworkDirection directionFor(Class<? extends Packet<?>> clazz) {
		if (clazz.equals(CustomPayloadC2SPacket.class)) {
			return PLAY_TO_SERVER;
		} else if (clazz.equals(CustomPayloadS2CPacket.class)) {
			return PLAY_TO_CLIENT;
		} else if (clazz.equals(LoginQueryResponseC2SPacket.class)) {
			return LOGIN_TO_SERVER;
		} else if (clazz.equals(LoginQueryRequestS2CPacket.class)) {
			return LOGIN_TO_CLIENT;
		} else {
			return null;
		}
	}

	@NotNull
	@SuppressWarnings("unchecked")
	private <T extends Packet<?>> ICustomPacket<T> construct() {
		switch (this) {
		case PLAY_TO_SERVER:
			return (ICustomPacket<T>) CustomPayloadC2SPacketAccessor.patchwork$create();
		case PLAY_TO_CLIENT:
			return (ICustomPacket<T>) CustomPayloadS2CPacketAccessor.patchwork$create();
		case LOGIN_TO_SERVER:
			throw new UnsupportedOperationException("login packets are not supported");
			// TODO: return (ICustomPacket<T>) LoginQueryResponseC2SPacketAccessor.patchwork$create();
		case LOGIN_TO_CLIENT:
			throw new UnsupportedOperationException("login packets are not supported");
			// TODO: return (ICustomPacket<T>) LoginQueryRequestS2CPacketAccessor.patchwork$create();
		default:
			throw new IllegalStateException("Unexpected NetworkDirection " + this + ", someone's been tampering with enums!");
		}
	}

	@NotNull
	public <T extends Packet<?>> ICustomPacket<T> buildPacket(Pair<PacketByteBuf, Integer> packetData, Identifier channelName) {
		ICustomPacket<T> packet = construct();

		packet.setName(channelName);
		packet.setData(packetData.getLeft());
		packet.setIndex(packetData.getRight());

		return packet;
	}
}
