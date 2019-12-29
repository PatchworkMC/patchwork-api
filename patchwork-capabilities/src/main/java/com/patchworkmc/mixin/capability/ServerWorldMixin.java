package com.patchworkmc.mixin.capability;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.world.ServerWorld;

import com.patchworkmc.impl.capability.CapabilityProviderHolder;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements CapabilityProviderHolder {

	@Inject(method = "<init>", at = @At("TAIL"))
	private void initializeCapabilities(CallbackInfo callbackInfo) {
		// TODO: IForgeDimension when?
		getCapabilityProvider().gatherCapabilities();
	}
}
