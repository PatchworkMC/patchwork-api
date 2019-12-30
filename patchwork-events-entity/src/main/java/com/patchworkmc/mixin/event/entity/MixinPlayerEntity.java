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

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import com.patchworkmc.impl.event.entity.EntityEvents;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {
	// TODO: Forge bug: PlayerEntity calls its super, so this event gets fired twice on the client.
	@Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
	private void hookDeath(DamageSource source, CallbackInfo callback) {
		LivingEntity entity = (LivingEntity) (Object) this;

		if (EntityEvents.onLivingDeath(entity, source)) {
			callback.cancel();
		}
	}

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
}
