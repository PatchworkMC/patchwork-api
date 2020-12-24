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

package net.patchworkmc.mixin.networking.packet;

import net.minecraftforge.fml.network.ICustomPacket;
import net.minecraftforge.fml.network.NetworkDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

@Mixin(CustomPayloadC2SPacket.class)
public class MixinCustomPayloadC2SPacket implements ICustomPacket<CustomPayloadC2SPacket> {
	@Shadow
	private PacketByteBuf data;

	@Shadow
	private Identifier channel;

	@Override
	public PacketByteBuf getInternalData() {
		return new PacketByteBuf(this.data.copy());
	}

	@Override
	public Identifier getName() {
		return channel;
	}

	@Override
	public void setName(Identifier channelName) {
		this.channel = channelName;
	}

	@Override
	public int getIndex() {
		// Forge: return Integer.MIN_VALUE if there is no 'int' field in the class
		return Integer.MIN_VALUE;
	}

	@Override
	public void setIndex(int index) {
		// Forge: NO-OP if there is no 'int' field in the class
	}

	@Override
	public void setData(PacketByteBuf data) {
		this.data = data;
	}

	@Override
	public NetworkDirection getDirection() {
		return NetworkDirection.PLAY_TO_SERVER;
	}

	@Override
	public CustomPayloadC2SPacket getThis() {
		return (CustomPayloadC2SPacket) (Object) this;
	}
}
