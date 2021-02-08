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

package net.patchworkmc.mixin.event.entity.old;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraftforge.common.extensions.IForgeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

import net.patchworkmc.impl.event.entity.EntityEventsOld;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {
	@Unique
	private float[] fallData;

	@Shadow
	protected int playerHitTimer;

	// TODO: Forge bug: PlayerEntity calls its super, so this event gets fired twice on the client.
	@Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
	private void hookDeath(DamageSource source, CallbackInfo callback) {
		LivingEntity entity = (LivingEntity) (Object) this;

		if (EntityEventsOld.onLivingDeath(entity, source)) {
			callback.cancel();
		}
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void hookUpdate(CallbackInfo callback) {
		LivingEntity entity = (LivingEntity) (Object) this;

		if (EntityEventsOld.onLivingUpdateEvent(entity)) {
			callback.cancel();
		}
	}

	@Inject(method = "damage", at = @At("HEAD"), cancellable = true)
	private void hookDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {
		LivingEntity entity = (LivingEntity) (Object) this;

		if (EntityEventsOld.onLivingAttack(entity, source, amount)) {
			callback.setReturnValue(false);
		}
	}

	// Shift back one because otherwise we inject after the value of damage is pushed onto the JVM stack, causing the modification to have no effect
	@ModifyVariable(method = "applyDamage", at = @At(value = "INVOKE", target = "net/minecraft/entity/LivingEntity.applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F", shift = At.Shift.BEFORE))
	private float hookApplyDamageForHurtEvent(float damage, DamageSource source) {
		LivingEntity entity = (LivingEntity) (Object) this;

		return EntityEventsOld.onLivingHurt(entity, source, damage);
	}

	@Inject(method = "applyDamage", at = @At(value = "INVOKE", target = "net/minecraft/entity/LivingEntity.applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F"), cancellable = true)
	private void hookApplyDamageForHurtEventCancel(DamageSource source, float damage, CallbackInfo info) {
		if (damage <= 0) {
			info.cancel();
		}
	}

	@ModifyVariable(method = "handleFallDamage", at = @At(value = "INVOKE", target = "net/minecraft/entity/Entity.handleFallDamage(FF)V", shift = At.Shift.BEFORE), ordinal = 0)
	private float hookHandleFallDamageDistance(float distance, float damageMultiplier) {
		return fallData[0];
	}

	@ModifyVariable(method = "handleFallDamage", at = @At(value = "INVOKE", target = "net/minecraft/entity/Entity.handleFallDamage(FF)V", shift = At.Shift.AFTER), ordinal = 1)
	private float hookHandleFallDamageMultiplier(float distance, float damageMultiplier) {
		return fallData[1];
	}

	@Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
	private void hookHandleFallDamageCancel(float distance, float damageMultiplier, CallbackInfo info) {
		LivingEntity entity = (LivingEntity) (Object) this;

		fallData = EntityEventsOld.onLivingFall(entity, distance, damageMultiplier);

		if (fallData == null) {
			info.cancel();
		}
	}

	/**
	 * Carry over the looting level between the two mixins for the drop method.
	 *
	 * <p>
	 * The drop method has roughly this effect:
	 *
	 * <pre>{@code
	 * protected void drop(DamageSource source) {
	 *     int lootingLevel = ...
	 *     // hookDropForCapturePre mixin
	 *     this.dropEquipment(source, lootingLevel, bl);
	 *     // FRAME CHOP 3
	 *     this.dropInventory();
	 *
	 *     // Added by forge, MUST be called after dropInventory:
	 *     sendEvent(lootingLevel);
	 * }
	 * }</pre>
	 *
	 * And thus we can't access the looting level from the end since it's local has
	 * been discarded. Thus we store it in a ThreadLocal, to keep track of it between
	 * the two methods.
	 * </p>
	 */
	@Unique
	private final ThreadLocal<Integer> dropLootingLevel = new ThreadLocal<>();

	@Inject(method = "drop", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;playerHitTimer : I"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void hookDropForCapturePre(DamageSource src, CallbackInfo info, int lootingLevel) {
		IForgeEntity forgeEntity = (IForgeEntity) this;
		forgeEntity.captureDrops(new ArrayList<>());

		dropLootingLevel.set(lootingLevel);
	}

	@Inject(method = "drop", at = @At("TAIL"))
	private void hookDropForDropsEvent(DamageSource src, CallbackInfo info) {
		LivingEntity entity = (LivingEntity) (Object) this;
		IForgeEntity forgeEntity = (IForgeEntity) this;
		Collection<ItemEntity> drops = forgeEntity.captureDrops(null);

		if (!EntityEventsOld.onLivingDrops(entity, src, drops, dropLootingLevel.get(), playerHitTimer > 0)) {
			for (ItemEntity item : drops) {
				forgeEntity.getEntity().world.spawnEntity(item);
			}
		}

		dropLootingLevel.remove();
	}

	// No shift, because we are specifically not modifying the value for this function call.
	// TODO: Forge patches a bit later into the function here, being inconsistent with their patch for PlayerEntity. For the moment, I don't feel like finding an injection point for that, and this may be a Forge bug?
	@ModifyVariable(method = "applyDamage", argsOnly = true, at = @At(value = "INVOKE", target = "net/minecraft/entity/LivingEntity.setAbsorptionAmount (F)V", ordinal = 0))
	private float hookApplyDamageForDamageEvent(float damage, DamageSource source) {
		LivingEntity entity = (LivingEntity) (Object) this;

		return EntityEventsOld.onLivingDamage(entity, source, damage);
	}
}
