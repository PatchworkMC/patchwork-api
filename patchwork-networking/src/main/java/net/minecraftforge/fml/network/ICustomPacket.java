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

import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

/**
 * Implemented on {@link CustomPayloadC2SPacket}, {@link CustomPayloadS2CPacket}, {@link LoginQueryRequestS2CPacket},
 * and {@link LoginQueryResponseC2SPacket}.
 */
public interface ICustomPacket<T extends Packet<?>> {
	PacketByteBuf getInternalData();

	/**
	 * Get the channel name.
	 */
	Identifier getName();

	/**
	 * Set the channel name.
	 * @param channelName
	 */
	void setName(Identifier channelName);

	int getIndex();

	void setIndex(int index);

	void setData(PacketByteBuf buffer);

	NetworkDirection getDirection();

	T getThis();
}
