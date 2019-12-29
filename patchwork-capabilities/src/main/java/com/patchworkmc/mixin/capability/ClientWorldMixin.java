package com.patchworkmc.mixin.capability;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.world.ClientWorld;

import com.patchworkmc.impl.capability.CapabilityProviderHolder;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin implements CapabilityProviderHolder {

	@Inject(method = "<init>", at = @At("TAIL"))
	private void initializeCapabilities(CallbackInfo callbackInfo) {
		// TODO: IForgeDimension when?
		getCapabilityProvider().gatherCapabilities();
	}
}
