package com.patchworkmc.mixin.networking.handler;

import net.minecraftforge.fml.network.ICustomPacket;
import net.minecraftforge.fml.network.NetworkHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.client.network.packet.TagQueryResponseS2CPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {

	@Shadow
	public abstract ClientConnection getConnection();

	@Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
	private void patchwork$hookCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo callback) {
		if(NetworkHooks.onCustomPayload((ICustomPacket<?>) packet, getConnection())) {
			callback.cancel();
		}
	}
}
