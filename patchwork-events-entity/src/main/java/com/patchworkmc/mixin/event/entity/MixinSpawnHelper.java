package com.patchworkmc.mixin.event.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.ViewableWorld;

import com.patchworkmc.impl.event.entity.EntityEvents;

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
	private static boolean hookCheckSpawn(MobEntity entity, IWorld world, SpawnType spawnType) {
		return EntityEvents.canEntitySpawnNaturally(entity, entity.world, entity.x, entity.y, entity.x, null, spawnType, playerDistanceStore);
	}

	@Redirect(method = "spawnEntitiesInChunk", at = @At(value = "INVOKE", target = "net/minecraft/entity/mob/MobEntity.canSpawn(Lnet/minecraft/world/ViewableWorld;)Z"))
	private static boolean disableVanillaLogic(MobEntity entity, ViewableWorld world) {
		return true;
	}

	@Redirect(method = "spawnEntitiesInChunk", at = @At(value = "INVOKE", target = "net/minecraft/entity/mob/MobEntity.initialize(Lnet/minecraft/world/IWorld;Lnet/minecraft/world/LocalDifficulty;Lnet/minecraft/entity/SpawnType;Lnet/minecraft/entity/EntityData;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/entity/EntityData;"))
	private static EntityData hookSpecialSpawn(MobEntity entity, IWorld world, LocalDifficulty localDifficulty, SpawnType spawnType, EntityData data, CompoundTag tag) {
		if (!EntityEvents.doSpecialSpawn(entity, world, entity.x, entity.y, entity.z, null, spawnType)) {
			return entity.initialize(world, localDifficulty, spawnType, data, tag);
		} else {
			return data;
		}
	}
}
