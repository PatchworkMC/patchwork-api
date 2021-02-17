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

import org.jetbrains.annotations.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.patchworkmc.impl.event.entity.EntityEventsOld;

@Mixin(EntityType.class)
public class MixinEntityType {
	private static final String SPAWN = "spawn(Lnet/minecraft/world/World;Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/text/Text;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/SpawnType;ZZ)Lnet/minecraft/entity/Entity;";

	@Inject(method = SPAWN, at = @At(value = "INVOKE", target = "net/minecraft/world/World.spawnEntity(Lnet/minecraft/entity/Entity;)Z"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void hookMobSpawns(World world, @Nullable CompoundTag itemTag, @Nullable Text name, @Nullable PlayerEntity player, BlockPos pos, SpawnReason type, boolean alignPosition, boolean bl, CallbackInfoReturnable<Entity> callback, Entity entity) {
		if (!(entity instanceof MobEntity)) {
			return;
		}

		MobEntity mob = (MobEntity) entity;

		if (EntityEventsOld.doSpecialSpawn(mob, world, pos.getX(), pos.getY(), pos.getZ(), null, type)) {
			callback.setReturnValue(null);
		}
	}
}
