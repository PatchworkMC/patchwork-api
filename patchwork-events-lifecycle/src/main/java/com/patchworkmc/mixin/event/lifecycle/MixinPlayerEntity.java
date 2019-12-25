package com.patchworkmc.mixin.event.lifecycle;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.patchworkmc.impl.event.lifecycle.LifecycleEvents;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {
	@Inject(method = "tick", at = @At("HEAD"))
	public void onPlayerPreTick(CallbackInfo callback) {
		LifecycleEvents.onPlayerPreTick((PlayerEntity)(Object)this);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void onPlayerPostTick(CallbackInfo callback) {
		LifecycleEvents.onPlayerPostTick((PlayerEntity)(Object)this);
	}
}
