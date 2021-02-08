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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

import net.patchworkmc.impl.event.entity.EntityEventsOld;

// CatEntity is intentionally omitted. See MinecraftForge/#7171
@Mixin({ParrotEntity.class, WolfEntity.class})
public abstract class MixinTameableEntitySubclass extends TameableEntity {
	protected MixinTameableEntitySubclass() {
		//noinspection ConstantConditions
		super(null, null);
	}

	@Redirect(method = "interactMob", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
	private int redirectTameCheck(Random random, int bound, PlayerEntity player, Hand hand) {
		int i = random.nextInt(bound);

		if (i != 0 || EntityEventsOld.onAnimalTame(this, player)) {
			return -1; // Check failed
		}

		return 0; // Check succeeds
	}
}
