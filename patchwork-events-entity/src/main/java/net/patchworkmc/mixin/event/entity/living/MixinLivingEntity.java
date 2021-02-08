package net.patchworkmc.mixin.event.entity.living;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.LivingEntity;

import net.patchworkmc.impl.event.entity.EntityEvents;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {
	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void patchwork$fireLivingUpdate(CallbackInfo callback) {
		LivingEntity entity = (LivingEntity) (Object) this;

		if (EntityEvents.onLivingUpdateEvent(entity)) {
			callback.cancel();
		}
	}
}
