package com.patchworkmc.mixin.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.patchworkmc.impl.event.entity.EntityEvents;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {
	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	private void hookInteractEntity(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> callback) {
		PlayerEntity player = (PlayerEntity)(Object)this;

		if(player.isSpectator()) {
			return;
		}

		int tries = 1;

		if(player.getEntityWorld().isClient) {
			// TODO: Mimicking a stupid forge bug. They fire this *twice* on the client for some reason.
			// I don't know why. It's hooked in ClientPlayerInteractionManager and in PlayerEntity.
			// ClientPlayerInteractionManager just calls directly into PlayerEntity's version.

			tries = 2;
		}

		for(int i = 0; i < tries; i++) {
			ActionResult result = EntityEvents.onInteractEntity(player, entity, hand);

			if(result != null) {
				callback.setReturnValue(result);
				return;
			}
		}
	}
}
