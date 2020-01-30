/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2020, 2019-2020
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

import com.patchworkmc.impl.event.entity.EntityEvents;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {
	// TODO: Forge bug: PlayerEntity calls its super, so this event gets fired twice on the client.
	@Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
	private void hookDeath(DamageSource source, CallbackInfo callback) {
		LivingEntity entity = (LivingEntity) (Object) this;

		if (EntityEvents.onLivingDeath(entity, source)) {
			callback.cancel();
		}
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void hookUpdate(CallbackInfo callback) {
		LivingEntity entity = (LivingEntity) (Object) this;

		if (EntityEvents.onLivingUpdateEvent(entity)) {
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
	@ModifyVariable(method = "applyDamage", at = @At(value = "INVOKE", target = "net/minecraft/entity/LivingEntity.applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F", shift = At.Shift.BEFORE))
	private float hookApplyDamageForHurtEvent(float damage, DamageSource source) {
		LivingEntity entity = (LivingEntity) (Object) this;

		return EntityEvents.onLivingHurt(entity, source, damage);
	}

	@Inject(method = "applyDamage", at = @At(value = "INVOKE", target = "net/minecraft/entity/LivingEntity.applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F"), cancellable = true)
	private void hookApplyDamageForHurtEventCancel(DamageSource source, float damage, CallbackInfo info) {
		if (damage <= 0) {
			info.cancel();
		}
	}
}
