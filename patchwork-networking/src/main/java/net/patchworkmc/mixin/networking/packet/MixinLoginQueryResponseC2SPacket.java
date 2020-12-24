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
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.util.Identifier;

@Mixin(LoginQueryResponseC2SPacket.class)
public class MixinLoginQueryResponseC2SPacket implements ICustomPacket<LoginQueryResponseC2SPacket> {
	@Shadow
	private PacketByteBuf response;

	@Shadow
	private int queryId;

	@Override
	public PacketByteBuf getInternalData() {
		return new PacketByteBuf(this.response.copy());
	}

	@Override
	public Identifier getName() {
		// Forge: return FMLLoginWrapper.WRAPPER if there is no channel
		return new Identifier("fml:loginwrapper");
	}

	@Override
	public void setName(Identifier channelName) {
		// Forge: NO-OP if there is no channel
	}

	@Override
	public int getIndex() {
		return queryId;
	}

	@Override
	public void setIndex(int index) {
		this.queryId = index;
	}

	@Override
	public void setData(PacketByteBuf data) {
		this.response = data;
	}

	@Override
	public NetworkDirection getDirection() {
		return NetworkDirection.LOGIN_TO_SERVER;
	}

	@Override
	public LoginQueryResponseC2SPacket getThis() {
		return (LoginQueryResponseC2SPacket) (Object) this;
	}
}
