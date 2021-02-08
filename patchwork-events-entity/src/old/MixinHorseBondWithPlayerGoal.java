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

import java.util.Random;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.ai.goal.HorseBondWithPlayerGoal;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;

import net.patchworkmc.impl.event.entity.EntityEventsOld;

@Mixin(HorseBondWithPlayerGoal.class)
public class MixinHorseBondWithPlayerGoal {
	@Shadow
	@Final
	private HorseBaseEntity horse;

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I", ordinal = 1))
	private int redirectHorseBondWithPlayerCheck(Random random, int bound) {
		int temper = horse.getTemper();
		int nextInt = random.nextInt(bound);

		if (nextInt < temper) {
			if (EntityEventsOld.onAnimalTame(horse, (PlayerEntity) horse.getPassengerList().get(0))) {
				return Integer.MAX_VALUE; // Force nextInt > temper
			}
		}

		return nextInt;
	}
}
