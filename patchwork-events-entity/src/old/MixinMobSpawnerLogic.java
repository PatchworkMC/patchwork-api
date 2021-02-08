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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.CollisionView;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.patchworkmc.impl.event.entity.EntityEventsOld;

@Mixin(MobSpawnerLogic.class)
public class MixinMobSpawnerLogic {
	@Redirect(method = "update", at = @At(value = "INVOKE", target = "net/minecraft/entity/mob/MobEntity.canSpawn(Lnet/minecraft/world/IWorld;Lnet/minecraft/entity/SpawnType;)Z"))
	private boolean spawnTestRedirect(MobEntity on, WorldAccess world, SpawnReason type) {
		MobSpawnerLogic spawner = (MobSpawnerLogic) (Object) this;

		return EntityEventsOld.canEntitySpawnFromSpawner(on, (World) world, on.x, on.y, on.z, spawner);
	}

	@Redirect(method = "update", at = @At(value = "INVOKE", target = "net/minecraft/entity/mob/MobEntity.canSpawn(Lnet/minecraft/world/CollisionView;)Z"))
	private boolean makeTheOtherMethodNotMessItUp(MobEntity on, CollisionView world) {
		return true;
	}
}
