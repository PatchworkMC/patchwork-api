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
