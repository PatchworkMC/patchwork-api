package com.patchworkmc.mixin.networking.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.server.network.packet.CustomPayloadC2SPacket;

@Mixin(CustomPayloadC2SPacket.class)
public interface CustomPayloadC2SPacketAccessor {
	@SuppressWarnings("PublicStaticMixinMember")
	@Invoker("<init>")
	public static CustomPayloadC2SPacket patchwork$create() {
		throw new AssertionError("Mixin not applied");
	}
}
