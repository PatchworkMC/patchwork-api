package com.patchworkmc.mixin.networking.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.network.packet.LoginQueryRequestS2CPacket;

@Mixin(LoginQueryRequestS2CPacket.class)
public interface LoginQueryRequestS2CPacketAccessor {
	@SuppressWarnings("PublicStaticMixinMember")
	@Invoker("<init>")
	public static LoginQueryRequestS2CPacket patchwork$create() {
		throw new AssertionError("Mixin not applied");
	}
}
