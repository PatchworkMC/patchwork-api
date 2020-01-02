package com.patchworkmc.mixin.networking.handler;

import net.minecraftforge.fml.network.ICustomPacket;
import net.minecraftforge.fml.network.NetworkHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;

@Mixin(ServerLoginNetworkHandler.class)
public abstract class MixinServerLoginNetworkHandler {
	@Shadow
	public abstract ClientConnection getConnection();

	@Inject(method = "onQueryResponse", at = @At("HEAD"), cancellable = true)
	private void patchwork$hookCustomPayload(LoginQueryResponseC2SPacket packet, CallbackInfo callback) {
		if(NetworkHooks.onCustomPayload((ICustomPacket<?>) packet, getConnection())) {
			callback.cancel();
		}
	}
}
