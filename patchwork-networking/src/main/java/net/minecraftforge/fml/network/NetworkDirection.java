package net.minecraftforge.fml.network;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceArrayMap;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.unsafe.UnsafeHacks;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.client.network.packet.LoginQueryRequestS2CPacket;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;


public enum NetworkDirection
{
	PLAY_TO_SERVER(NetworkEvent.ClientCustomPayloadEvent::new, LogicalSide.CLIENT, CustomPayloadC2SPacket.class, 1),
	PLAY_TO_CLIENT(NetworkEvent.ServerCustomPayloadEvent::new, LogicalSide.SERVER, CustomPayloadS2CPacket.class, 0),
	LOGIN_TO_SERVER(NetworkEvent.ClientCustomPayloadLoginEvent::new, LogicalSide.CLIENT, LoginQueryResponseC2SPacket.class, 3),
	LOGIN_TO_CLIENT(NetworkEvent.ServerCustomPayloadLoginEvent::new, LogicalSide.SERVER, LoginQueryRequestS2CPacket.class, 2);

	private final BiFunction<ICustomPacket<?>, Supplier<NetworkEvent.Context>, NetworkEvent> eventSupplier;
	private final LogicalSide logicalSide;
	private final Class<? extends Packet> packetClass;
	private final int otherWay;

	private static final Reference2ReferenceArrayMap<Class<? extends Packet>, NetworkDirection> packetLookup;

	static {
		packetLookup = Stream.of(values()).
			collect(Collectors.toMap(NetworkDirection::getPacketClass, Function.identity(), (m1,m2)->m1, Reference2ReferenceArrayMap::new));
	}

	NetworkDirection(BiFunction<ICustomPacket<?>, Supplier<NetworkEvent.Context>, NetworkEvent> eventSupplier, LogicalSide logicalSide, Class<? extends Packet> clazz, int i)
	{
		this.eventSupplier = eventSupplier;
		this.logicalSide = logicalSide;
		this.packetClass = clazz;
		this.otherWay = i;
	}

	private Class<? extends Packet> getPacketClass() {
		return packetClass;
	}
	public static <T extends ICustomPacket<?>> NetworkDirection directionFor(Class<T> customPacket)
	{
		return packetLookup.get(customPacket);
	}

	public NetworkDirection reply() {
		return NetworkDirection.values()[this.otherWay];
	}
	public NetworkEvent getEvent(final ICustomPacket<?> buffer, final Supplier<NetworkEvent.Context> manager) {
		return this.eventSupplier.apply(buffer, manager);
	}

	public LogicalSide getOriginationSide()
	{
		return logicalSide;
	}

	public LogicalSide getReceptionSide() { return reply().logicalSide; };

	public <T extends Packet<?>> ICustomPacket<T> buildPacket(Pair<PacketByteBuf,Integer> packetData, Identifier channelName)
	{
		ICustomPacket<T> packet = (ICustomPacket<T>) UnsafeHacks.newInstance(getPacketClass()); // TODO: coderbot said something about a factory mixin
		packet.setName(channelName);
		packet.setData(packetData.getLeft());
		packet.setIndex(packetData.getRight());
		return packet;
	}
}
