package com.patchworkmc.mixin.networking.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.network.packet.CustomPayloadS2CPacket;

@Mixin(CustomPayloadS2CPacket.class)
public interface CustomPayloadS2CPacketAccessor {

	@SuppressWarnings("PublicStaticMixinMember")
	@Invoker("<init>")
	public static CustomPayloadS2CPacket patchwork$create() {
		throw new AssertionError("Mixin not applied");
	}
}
