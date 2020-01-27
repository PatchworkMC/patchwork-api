/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2019, 2019
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.patchworkmc.mixin.event.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import com.patchworkmc.impl.event.entity.EntityEvents;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {
    @Shadow
    public PlayerAbilities abilities;

	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	private void hookInteractEntity(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> callback) {
		PlayerEntity player = (PlayerEntity) (Object) this;

		if (player.isSpectator()) {
			return;
		}

		int tries = 1;

		if (player.getEntityWorld().isClient) {
			// TODO: Mimicking a stupid forge bug. They fire this *twice* on the client for some reason.
			// I don't know why. It's hooked in ClientPlayerInteractionManager and in PlayerEntity.
			// ClientPlayerInteractionManager just calls directly into PlayerEntity's version.

			tries = 2;
		}

		for (int i = 0; i < tries; i++) {
			ActionResult result = EntityEvents.onInteractEntity(player, entity, hand);

			if (result != null) {
				callback.setReturnValue(result);
				return;
			}
		}
	}

	// TODO: Forge bug: PlayerEntity calls its super, so this event gets fired twice on the client.
	@Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
	private void hookDeath(DamageSource source, CallbackInfo callback) {
		LivingEntity entity = (LivingEntity) (Object) this;

		if (EntityEvents.onLivingDeath(entity, source)) {
			callback.cancel();
		}
	}

	@Inject(method = "damage", at = @At("HEAD"), cancellable = true)
	private void hookDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {
		LivingEntity entity = (LivingEntity) (Object) this;

		if (EntityEvents.onLivingAttack(entity, source, amount)) {
			callback.setReturnValue(false);
		}
	}

	// Shift back one because otherwise we inject after the value of damage is pushed onto the JVM stack, causing the modification to have no effect
	@ModifyVariable(method = "applyDamage", at = @At(value = "INVOKE", target = "net/minecraft/entity/player/PlayerEntity.applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F", shift = At.Shift.BEFORE))
	private float hookApplyDamageForHurtEvent(float damage, DamageSource source) {
		LivingEntity entity = (LivingEntity) (Object) this;

		return EntityEvents.onLivingHurt(entity, source, damage);
	}

	@Inject(method = "applyDamage", at = @At(value = "INVOKE", target = "net/minecraft/entity/player/PlayerEntity.applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F"), cancellable = true)
	private void hookApplyDamageForHurtEventCancel(DamageSource source, float damage, CallbackInfo info) {
		if (damage <= 0) {
			info.cancel();
		}
	}

	@Inject(method = "handleFallDamage", at = @At("RETURN"), cancellable = true)
	private void hookHandleFallDamage(float distance, float damageMultiplier, CallbackInfo info) {
		if (!abilities.allowFlying) {
			info.cancel();
			return;
		}

		PlayerEntity player = (PlayerEntity) (Object) this;
		EntityEvents.onPlayerFall(player, distance, damageMultiplier);
	}
}
