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

package net.minecraftforge.event;

import javax.annotation.Nullable;

import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.eventbus.api.Event;

import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.IWorld;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;

import net.patchworkmc.impl.capability.CapabilityEvents;
import net.patchworkmc.impl.event.entity.EntityEvents;

/*
 * Note: this class is intended for mod use only, to dispatch to the implementations kept in their own modules.
 * Do not keep implementation details here, methods should be thin wrappers around methods in other modules.
 */
public class ForgeEventFactory {
	@Nullable
	public static <T> CapabilityDispatcher gatherCapabilities(Class<? extends T> type, T provider) {
		return gatherCapabilities(type, provider, null);
	}

	@Nullable
	public static <T> CapabilityDispatcher gatherCapabilities(Class<? extends T> type, T provider, @Nullable ICapabilityProvider parent) {
		return CapabilityEvents.gatherCapabilities(type, provider, parent);
	}

	public static Event.Result canEntitySpawn(MobEntity entity, IWorld world, double x, double y, double z, MobSpawnerLogic spawner, SpawnType spawnReason) {
		return EntityEvents.canEntitySpawn(entity, world, x, y, z, spawner, spawnReason);
	}

	public static boolean canEntitySpawnSpawner(MobEntity entity, World world, float x, float y, float z, MobSpawnerLogic spawner) {
		return EntityEvents.canEntitySpawnFromSpawner(entity, world, x, y, z, spawner);
	}

	public static void onPlayerFall(PlayerEntity player, float distance, float multiplier) {
		EntityEvents.onFlyablePlayerFall(player, distance, multiplier);
	}

	public static boolean doSpecialSpawn(MobEntity entity, World world, float x, float y, float z, MobSpawnerLogic spawner, SpawnType spawnReason) {
		return EntityEvents.doSpecialSpawn(entity, world, x, y, z, spawner, spawnReason);
	}
}
