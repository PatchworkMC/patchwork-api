package com.patchworkmc.mixin.extension;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntityType;

import com.patchworkmc.impl.extension.PatchworkEntityTypeBuilderExtensions;
import com.patchworkmc.impl.extension.PatchworkEntityTypeExtensions;

@Mixin(EntityType.Builder.class)
public class MixinEntityTypeBuilder implements PatchworkEntityTypeBuilderExtensions {
	@Unique
	private Integer updateInterval = null;
	@Unique
	private Integer trackingRange = null;
	@Unique
	private Boolean shouldRecieveVelocityUpdates = null;

	// TODO potential conflict with patchwork-vanilla-patches
	@Inject(method = "build", at = @At("RETURN"))
	private void onBuildReturn(String id, CallbackInfoReturnable<EntityType> cir) {
		PatchworkEntityTypeExtensions type = (PatchworkEntityTypeExtensions) cir.getReturnValue();

		if (updateInterval != null) {
			type.setUpdateInterval(updateInterval);
		}

		if (trackingRange != null) {
			type.setTrackingRange(trackingRange);
		}

		if (shouldRecieveVelocityUpdates != null) {
			type.setShouldReceiveVelocityUpdates(shouldRecieveVelocityUpdates);
		}
	}

	@Override
	public EntityType.Builder setUpdateInterval(int interval) {
		this.updateInterval = interval;
		return (EntityType.Builder) (Object) this;
	}

	@Override
	public EntityType.Builder setTrackingRange(int range) {
		this.trackingRange = range;
		return (EntityType.Builder) (Object) this;
	}

	@Override
	public EntityType.Builder setShouldReceiveVelocityUpdates(boolean value) {
		this.shouldRecieveVelocityUpdates = value;
		return (EntityType.Builder) (Object) this;
	}
}
