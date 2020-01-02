package com.patchworkmc.mixin.networking.handler;

import net.minecraftforge.fml.network.ICustomPacket;
import net.minecraftforge.fml.network.NetworkHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class MixinServerPlayNetworkHandler {
	@Shadow
	public abstract ClientConnection getConnection();

	@Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
	private void patchwork$hookCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo callback) {
		if(NetworkHooks.onCustomPayload((ICustomPacket<?>) packet, getConnection())) {
			callback.cancel();
		}
	}
}
