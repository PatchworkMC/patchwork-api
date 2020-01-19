package com.patchworkmc.mixin.extension;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntityType;

import com.patchworkmc.impl.extension.PatchworkEntityTypeExtensions;

@Mixin(EntityType.class)
public class MixinEntityType implements PatchworkEntityTypeExtensions {
	@Unique
	private Integer updateInterval = null;
	@Unique
	private Integer trackingRange = null;
	@Unique
	private Boolean shouldRecieveVelocityUpdates = null;

	@Inject(method = "getMaxTrackDistance", at = @At("HEAD"), cancellable = true)
	private void hookGetMaxTrackingDistance(CallbackInfoReturnable<Integer> cir) {
		if (trackingRange != null) {
			cir.setReturnValue(trackingRange);
		}
	}

	@Inject(method = "getTrackTickInterval", at = @At("HEAD"), cancellable = true)
	private void hookGetTrackTickInterval(CallbackInfoReturnable<Integer> cir) {
		if (updateInterval != null) {
			cir.setReturnValue(updateInterval);
		}
	}

	@Inject(method = "alwaysUpdateVelocity", at = @At("HEAD"), cancellable = true)
	private void hookAlwaysUpdateVelocity(CallbackInfoReturnable<Boolean> cir) {
		if (shouldRecieveVelocityUpdates != null) {
			cir.setReturnValue(shouldRecieveVelocityUpdates);
		}
	}

	@Override
	public void setUpdateInterval(int interval) {
		this.updateInterval = interval;
	}

	@Override
	public void setTrackingRange(int range) {
		this.trackingRange = range;
	}

	@Override
	public void setShouldReceiveVelocityUpdates(boolean value) {
		this.shouldRecieveVelocityUpdates = value;
	}
}
