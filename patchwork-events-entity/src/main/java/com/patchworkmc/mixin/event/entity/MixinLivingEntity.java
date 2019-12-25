package com.patchworkmc.mixin.event.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.patchworkmc.impl.event.entity.EntityEvents;

// TODO: Forge bug: PlayerEntity calls its super, so this event gets fired twice on the client.
@Mixin({LivingEntity.class, PlayerEntity.class, ServerPlayerEntity.class})
public class MixinLivingEntity {
	@Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
	private void hookDeath(DamageSource source, CallbackInfo callback) {
		LivingEntity entity = (LivingEntity)(Object)this;

		if(EntityEvents.onLivingDeath(entity, source)) {
			callback.cancel();
		}
	}
}
