package com.patchworkmc.mixin.networking.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;

@Mixin(LoginQueryResponseC2SPacket.class)
public interface LoginQueryResponseC2SPacketAccessor {
	@SuppressWarnings("PublicStaticMixinMember")
	@Invoker("<init>")
	public static LoginQueryResponseC2SPacket patchwork$create() {
		throw new AssertionError("Mixin not applied");
	}
}
