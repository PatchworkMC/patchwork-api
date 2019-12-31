package net.minecraftforge.fml.network;

import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.client.network.packet.LoginQueryRequestS2CPacket;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

/**
 * Implemented on {@link CustomPayloadC2SPacket}, {@link CustomPayloadS2CPacket}, {@link LoginQueryRequestS2CPacket},
 * and {@link LoginQueryResponseC2SPacket}.
 */
public interface ICustomPacket<T extends Packet<?>> {
	PacketByteBuf getInternalData();

	/**
	 * Get the channel name
	 */
	Identifier getName();

	/**
	 * Set the channel name
	 * @param channelName
	 */
	void setName(Identifier channelName);

	int getIndex();

	void setIndex(int index);

	void setData(PacketByteBuf buffer);

	NetworkDirection getDirection();

	T getThis();
}
