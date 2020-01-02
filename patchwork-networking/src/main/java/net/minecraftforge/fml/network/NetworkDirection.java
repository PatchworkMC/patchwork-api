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
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceArrayMap;
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
	PLAY_TO_SERVER(NetworkEvent.ClientCustomPayloadEvent::new, LogicalSide.CLIENT, CustomPayloadC2SPacket.class, 1),
	PLAY_TO_CLIENT(NetworkEvent.ServerCustomPayloadEvent::new, LogicalSide.SERVER, CustomPayloadS2CPacket.class, 0),
	LOGIN_TO_SERVER(NetworkEvent.ClientCustomPayloadLoginEvent::new, LogicalSide.CLIENT, LoginQueryResponseC2SPacket.class, 3),
	LOGIN_TO_CLIENT(NetworkEvent.ServerCustomPayloadLoginEvent::new, LogicalSide.SERVER, LoginQueryRequestS2CPacket.class, 2);

	private static final Reference2ReferenceArrayMap<Class<? extends Packet>, NetworkDirection> packetLookup;

	static {
		packetLookup = Stream.of(values())
			.collect(Collectors.toMap(NetworkDirection::getPacketClass, Function.identity(), (m1, m2) -> m1, Reference2ReferenceArrayMap::new));
	}

	private final BiFunction<ICustomPacket<?>, Supplier<NetworkEvent.Context>, NetworkEvent> eventSupplier;
	private final LogicalSide logicalSide;
	private final Class<? extends Packet> packetClass;
	private final int otherWay;

	NetworkDirection(BiFunction<ICustomPacket<?>, Supplier<NetworkEvent.Context>, NetworkEvent> eventSupplier, LogicalSide logicalSide, Class<? extends Packet> clazz, int i) {
		this.eventSupplier = eventSupplier;
		this.logicalSide = logicalSide;
		this.packetClass = clazz;
		this.otherWay = i;
	}

	public static <T extends ICustomPacket<?>> NetworkDirection directionFor(Class<T> customPacket) {
		return packetLookup.get(customPacket);
	}

	private Class<? extends Packet> getPacketClass() {
		return packetClass;
	}

	public NetworkDirection reply() {
		return NetworkDirection.values()[this.otherWay];
	}

	public NetworkEvent getEvent(final ICustomPacket<?> buffer, final Supplier<NetworkEvent.Context> manager) {
		return this.eventSupplier.apply(buffer, manager);
	}

	public LogicalSide getOriginationSide() {
		return logicalSide;
	}

	public LogicalSide getReceptionSide() {
		return reply().logicalSide;
	}

	public <T extends Packet<?>> ICustomPacket<T> buildPacket(Pair<PacketByteBuf, Integer> packetData, Identifier channelName) {
		ICustomPacket<T> packet = null;
		Class<? extends Packet> packetClass = getPacketClass();

		if (packetClass.equals(CustomPayloadC2SPacket.class)) {
			packet = (ICustomPacket<T>) CustomPayloadC2SPacketAccessor.patchwork$create();
		} else if (packetClass.equals(CustomPayloadS2CPacket.class)) {
			packet = (ICustomPacket<T>) CustomPayloadS2CPacketAccessor.patchwork$create();
		} else if (packetClass.equals(LoginQueryRequestS2CPacket.class)) {
			packet = (ICustomPacket<T>) LoginQueryRequestS2CPacketAccessor.patchwork$create();
		} else if (packetClass.equals(LoginQueryResponseC2SPacket.class)) {
			packet = (ICustomPacket<T>) LoginQueryResponseC2SPacketAccessor.patchwork$create();
		}

		packet.setName(channelName);
		packet.setData(packetData.getLeft());
		packet.setIndex(packetData.getRight());
		return packet;
	}
}
