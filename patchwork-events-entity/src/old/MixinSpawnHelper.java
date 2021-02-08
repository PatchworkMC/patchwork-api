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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.CollisionView;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.WorldAccess;
import net.patchworkmc.impl.event.entity.EntityEventsOld;

@Mixin(SpawnHelper.class)
public class MixinSpawnHelper {
	@Unique
	private static double playerDistanceStore;

	//We re-implement the vanilla logic in the event, disable it here. Also capture the player distance.
	@Redirect(method = "spawnEntitiesInChunk", at = @At(value = "INVOKE", target = "net/minecraft/entity/mob/MobEntity.canImmediatelyDespawn(D)Z"))
	private static boolean disableVanillaLogicAndCaptureDistance(MobEntity entity, double distFromPlayer) {
		playerDistanceStore = distFromPlayer;
		return false;
	}

	@Redirect(method = "spawnEntitiesInChunk", at = @At(value = "INVOKE", target = "net/minecraft/entity/mob/MobEntity.canSpawn(Lnet/minecraft/world/IWorld;Lnet/minecraft/entity/SpawnType;)Z"))
	private static boolean hookCheckSpawn(MobEntity entity, WorldAccess world, SpawnReason spawnType) {
		return EntityEventsOld.canEntitySpawnNaturally(entity, entity.world, entity.x, entity.y, entity.x, null, spawnType, playerDistanceStore);
	}

	@Redirect(method = "spawnEntitiesInChunk", at = @At(value = "INVOKE", target = "net/minecraft/entity/mob/MobEntity.canSpawn(Lnet/minecraft/world/CollisionView;)Z"))
	private static boolean disableVanillaLogic(MobEntity entity, CollisionView world) {
		return true;
	}

	@Redirect(method = "spawnEntitiesInChunk", at = @At(value = "INVOKE", target = "net/minecraft/entity/mob/MobEntity.initialize(Lnet/minecraft/world/IWorld;Lnet/minecraft/world/LocalDifficulty;Lnet/minecraft/entity/SpawnType;Lnet/minecraft/entity/EntityData;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/entity/EntityData;"))
	private static EntityData hookSpecialSpawn(MobEntity entity, WorldAccess world, LocalDifficulty localDifficulty, SpawnReason spawnType, EntityData data, CompoundTag tag) {
		if (!EntityEventsOld.doSpecialSpawn(entity, world, entity.x, entity.y, entity.z, null, spawnType)) {
			return entity.initialize(world, localDifficulty, spawnType, data, tag);
		} else {
			return data;
		}
	}
}
