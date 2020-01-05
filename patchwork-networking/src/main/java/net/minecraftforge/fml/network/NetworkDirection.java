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

import java.util.function.BiFunction;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.fml.LogicalSide;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.client.network.packet.LoginQueryRequestS2CPacket;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import com.patchworkmc.mixin.networking.accessor.CustomPayloadC2SPacketAccessor;
import com.patchworkmc.mixin.networking.accessor.CustomPayloadS2CPacketAccessor;
import com.patchworkmc.mixin.networking.accessor.LoginQueryRequestS2CPacketAccessor;
import com.patchworkmc.mixin.networking.accessor.LoginQueryResponseC2SPacketAccessor;

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

	@Nonnull
	public NetworkDirection reply() {
		return NetworkDirection.values()[this.otherWay];
	}

	public NetworkEvent getEvent(final ICustomPacket<?> buffer, final Supplier<NetworkEvent.Context> manager) {
		return this.eventSupplier.apply(buffer, manager);
	}

	@Nonnull
	public LogicalSide getOriginationSide() {
		return logicalSide;
	}

	@Nonnull
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

	@Nonnull
	@SuppressWarnings("unchecked")
	private <T extends Packet<?>> ICustomPacket<T> construct() {
		switch (this) {
		case PLAY_TO_SERVER:
			return (ICustomPacket<T>) CustomPayloadC2SPacketAccessor.patchwork$create();
		case PLAY_TO_CLIENT:
			return (ICustomPacket<T>) CustomPayloadS2CPacketAccessor.patchwork$create();
		case LOGIN_TO_SERVER:
			return (ICustomPacket<T>) LoginQueryResponseC2SPacketAccessor.patchwork$create();
		case LOGIN_TO_CLIENT:
			return (ICustomPacket<T>) LoginQueryRequestS2CPacketAccessor.patchwork$create();
		default:
			throw new IllegalStateException("Unexpected NetworkDirection " + this + ", someone's been tampering with enums!");
		}
	}

	@Nonnull
	public <T extends Packet<?>> ICustomPacket<T> buildPacket(Pair<PacketByteBuf, Integer> packetData, Identifier channelName) {
		ICustomPacket<T> packet = construct();

		packet.setName(channelName);
		packet.setData(packetData.getLeft());
		packet.setIndex(packetData.getRight());

		return packet;
	}
}
