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

package net.patchworkmc.mixin.event.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

import net.patchworkmc.impl.event.entity.EntityEvents;

// CatEntity is intentionally omitted. See MinecraftForge/#7171
@Mixin({ParrotEntity.class, WolfEntity.class})
public abstract class MixinTameableEntitySubclass extends TameableEntity {
	protected MixinTameableEntitySubclass() {
		//noinspection ConstantConditions
		super(null, null);
	}

	@Inject(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/TameableEntity;setOwner(Lnet/minecraft/entity/player/PlayerEntity;)V"), cancellable = true)
	private void onTamedMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<Boolean> cir) {
		if (EntityEvents.onAnimalTame(this, player)) {
			this.showEmoteParticle(false);
			this.world.sendEntityStatus(this, (byte) 6);
			cir.setReturnValue(true);
		}
	}
}
