package com.patchworkmc.mixin.networking.packet;

import net.minecraftforge.fml.network.ICustomPacket;
import net.minecraftforge.fml.network.NetworkDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.network.packet.LoginQueryRequestS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

@Mixin(LoginQueryRequestS2CPacket.class)
public class MixinLoginQueryRequestS2CPacket implements ICustomPacket<LoginQueryRequestS2CPacket> {
	@Shadow
	private PacketByteBuf payload;

	@Shadow
	private Identifier channel;

	@Shadow
	private int queryId;

	@Override
	public PacketByteBuf getInternalData() {
		return payload;
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
		return queryId;
	}

	@Override
	public void setIndex(int index) {
		this.queryId = index;
	}

	@Override
	public void setData(PacketByteBuf data) {
		this.payload = data;
	}

	@Override
	public NetworkDirection getDirection() {
		return NetworkDirection.LOGIN_TO_CLIENT;
	}

	@Override
	public LoginQueryRequestS2CPacket getThis() {
		return (LoginQueryRequestS2CPacket) (Object) this;
	}
}
