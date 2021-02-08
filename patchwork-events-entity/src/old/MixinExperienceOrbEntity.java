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
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ExperienceOrbEntity;

@Mixin(ExperienceOrbEntity.class)
public class MixinExperienceOrbEntity {
	// After checking we're on the server and the player is ready to pick up the orb, the first
	// thing the target method does is set experiencePickUpDelay, hence hook just before that.
	@Inject(method = "onPlayerCollision", cancellable = true, at = @At(value = "FIELD", opcode = Opcodes.H_PUTFIELD, ordinal = 0,
			target = "net/minecraft/entity/player/PlayerEntity.experiencePickUpDelay:I"))
	private void hookOnPlayerCollisionForPickup(PlayerEntity player, CallbackInfo ci) {
		@SuppressWarnings("ConstantConditions")
		ExperienceOrbEntity entity = (ExperienceOrbEntity) (Object) this;

		if (MinecraftForge.EVENT_BUS.post(new PlayerPickupXpEvent(player, entity))) {
			ci.cancel();
		}
	}
}
