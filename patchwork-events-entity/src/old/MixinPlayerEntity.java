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

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import net.patchworkmc.impl.event.entity.EntityEventsOld;

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
			ActionResult result = EntityEventsOld.onInteractEntity(player, entity, hand);

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

		if (EntityEventsOld.onLivingDeath(entity, source)) {
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
	@ModifyVariable(method = "applyDamage", at = @At(value = "INVOKE", target = "net/minecraft/entity/player/PlayerEntity.applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F", shift = At.Shift.BEFORE))
	private float hookApplyDamageForHurtEvent(float damage, DamageSource source) {
		LivingEntity entity = (LivingEntity) (Object) this;

		return EntityEventsOld.onLivingHurt(entity, source, damage);
	}

	@Inject(method = "applyDamage", at = @At(value = "INVOKE", target = "net/minecraft/entity/player/PlayerEntity.applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F"), cancellable = true)
	private void hookApplyDamageForHurtEventCancel(DamageSource source, float damage, CallbackInfo info) {
		if (damage <= 0) {
			info.cancel();
		}
	}

	@Inject(method = "handleFallDamage", at = @At("RETURN"))
	private void hookHandleFallDamage(float distance, float damageMultiplier, CallbackInfo info) {
		if (abilities.allowFlying) {
			PlayerEntity player = (PlayerEntity) (Object) this;
			EntityEventsOld.onFlyablePlayerFall(player, distance, damageMultiplier);
		}
	}

	// No shift, because we are specifically not modifying the value for this function call.
	@ModifyVariable(method = "applyDamage", argsOnly = true, at = @At(value = "INVOKE", target = "net/minecraft/entity/player/PlayerEntity.setAbsorptionAmount (F)V", ordinal = 0))
	private float hookApplyDamageForDamageEvent(float damage, DamageSource source) {
		LivingEntity entity = (LivingEntity) (Object) this;

		return EntityEventsOld.onLivingDamage(entity, source, damage);
	}

	@Inject(method = "attack(Lnet/minecraft/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
	private void onAttackEntity(Entity target, CallbackInfo callback) {
		PlayerEntity player = (PlayerEntity) (Object) this;

		if (!EntityEventsOld.attackEntity(player, target)) {
			callback.cancel();
		}
	}

	@ModifyVariable(method = "addExperience", at = @At("HEAD"), ordinal = 0)
	private int onAddExperience(int points) {
		@SuppressWarnings("ConstantConditions")
		PlayerEntity player = (PlayerEntity) (Object) this;

		PlayerXpEvent.XpChange event = new PlayerXpEvent.XpChange(player, points);
		MinecraftForge.EVENT_BUS.post(event);

		// The only effect of passing in zero is a call to addScore(0), which shouldn't have any effect.
		return event.isCanceled() ? 0 : event.getAmount();
	}

	@ModifyVariable(method = "addExperienceLevels", at = @At("HEAD"), ordinal = 0)
	private int onAddExperienceLevels(int levels) {
		@SuppressWarnings("ConstantConditions")
		PlayerEntity player = (PlayerEntity) (Object) this;

		PlayerXpEvent.LevelChange event = new PlayerXpEvent.LevelChange(player, levels);
		MinecraftForge.EVENT_BUS.post(event);

		// There are no effects from passing in zero levels, so do that if we've been canceled
		return event.isCanceled() ? 0 : event.getLevels();
	}

	@Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;",
			at = @At(value = "RETURN", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void onDropItem(ItemStack itemStack, boolean bl, boolean bl2, CallbackInfoReturnable<ItemEntity> ci, double y, ItemEntity itemEntity) {
		// Note: This is implemented slightly differently from forge, since forge only calls this event on
		// dropSelectedItem(boolean) and dropItem(ItemStack, boolean), but this way makes it much nicer to implement
		// and should produce the same behavior for modders

		if (EntityEventsOld.onPlayerTossEvent((PlayerEntity) (Object) this, itemEntity)) {
			ci.setReturnValue(null);
		}
	}
}
