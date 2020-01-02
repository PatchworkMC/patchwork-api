package com.patchworkmc.mixin.networking.packet;

import net.minecraftforge.fml.network.ICustomPacket;
import net.minecraftforge.fml.network.NetworkDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

@Mixin(CustomPayloadS2CPacket.class)
public class MixinCustomPayloadS2CPacket implements ICustomPacket<CustomPayloadS2CPacket> {
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
		return NetworkDirection.PLAY_TO_CLIENT;
	}

	@Override
	public CustomPayloadS2CPacket getThis() {
		return (CustomPayloadS2CPacket) (Object) this;
	}
}
