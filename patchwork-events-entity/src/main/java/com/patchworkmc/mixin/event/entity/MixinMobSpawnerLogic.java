package com.patchworkmc.mixin.event.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.IWorld;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

import com.patchworkmc.impl.event.entity.EntityEvents;

@Mixin(MobSpawnerLogic.class)
public class MixinMobSpawnerLogic {
	@Redirect(method = "update", at = @At(value = "INVOKE", target = "net/minecraft/entity/mob/MobEntity.canSpawn(Lnet/minecraft/world/IWorld;Lnet/minecraft/entity/SpawnType;)Z"))
	private boolean spawnTestRedirect(MobEntity on, IWorld world, SpawnType type) {
		MobSpawnerLogic spawner = (MobSpawnerLogic) (Object) this;

		return EntityEvents.canEntitySpawnFromSpawner(on, (World) world, on.x, on.y, on.z, spawner);
	}

	@Redirect(method = "update", at = @At(value = "INVOKE", target = "net/minecraft/entity/mob/MobEntity.canSpawn(Lnet/minecraft/world/ViewableWorld;)Z"))
	private boolean makeTheOtherMethodNotMessItUp(MobEntity on, ViewableWorld world) {
		return true;
	}
}
