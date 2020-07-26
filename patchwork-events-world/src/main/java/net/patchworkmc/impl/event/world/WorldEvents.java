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

package net.patchworkmc.impl.event.world;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.SaplingGrowTreeEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.LevelInfo;

public class WorldEvents {
	public static boolean onCreateWorldSpawn(IWorld world, LevelInfo settings) {
		return MinecraftForge.EVENT_BUS.post(new WorldEvent.CreateSpawnPosition(world, settings));
	}

	public static List<Biome.SpawnEntry> getPotentialSpawns(IWorld world, EntityCategory type, BlockPos pos, List<Biome.SpawnEntry> oldSpawns) {
		WorldEvent.PotentialSpawns event = new WorldEvent.PotentialSpawns(world, type, pos, oldSpawns);

		if (MinecraftForge.EVENT_BUS.post(event)) {
			return Collections.emptyList();
		}

		return event.getList();
	}

	public static void onWorldLoad(IWorld world) {
		MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(world));
	}

	public static void onWorldUnload(IWorld world) {
		MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(world));
	}

	public static void onWorldSave(IWorld world) {
		MinecraftForge.EVENT_BUS.post(new WorldEvent.Save(world));
	}

	public static boolean onFarmlandTrample(World world, BlockPos pos, BlockState state, float fallDistance, Entity entity) {
		// TODO: In forge, the possibility of trampling is handled by IForgeEntity.canTrample
		// Maybe there's a good way to reconcile that to not break any Fabric mods trying to
		// manipulate crop trampling, but for now I just let the vanilla check do it's thing.
		BlockEvent.FarmlandTrampleEvent event = new BlockEvent.FarmlandTrampleEvent(world, pos, state, fallDistance, entity);
		MinecraftForge.EVENT_BUS.post(event);
		return !event.isCanceled();
	}

	public static boolean onSaplingGrowTree(IWorld world, Random rand, BlockPos pos) {
		SaplingGrowTreeEvent event = new SaplingGrowTreeEvent(world, rand, pos);
		MinecraftForge.EVENT_BUS.post(event);
		return event.getResult() != Event.Result.DENY;
	}
}
