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

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.event.world.WorldEvent;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.world.World;
import net.minecraft.entity.EntityCategory;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.GameMode;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelInfo;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;

public class WorldEvents implements ModInitializer {
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

	/**
	 * Called by Mixin and ForgeHooks.
	 * @return experience dropped, -1 = block breaking is cancelled.
	 */
	public static int onBlockBreakEvent(World world, GameMode gameMode, ServerPlayerEntity player, BlockPos pos) {
		// Logic from tryHarvestBlock for pre-canceling the event
		boolean preCancelEvent = false;

		ItemStack itemstack = player.getMainHandStack();

		if (!itemstack.isEmpty() && !itemstack.getItem().canMine(world.getBlockState(pos), world, pos, player)) {
			preCancelEvent = true;
		}

		// method_21701 => canMine
		// Isn't the function really canNotMine?

		if (player.method_21701(world, pos, gameMode)) {
			preCancelEvent = true;
		}

		// Tell client the block is gone immediately then process events
		if (world.getBlockEntity(pos) == null) {
			player.networkHandler.sendPacket(new BlockUpdateS2CPacket(EmptyBlockView.INSTANCE, pos));
		}

		// Post the block break event
		BlockState state = world.getBlockState(pos);
		BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, state, player);
		event.setCanceled(preCancelEvent);
		MinecraftForge.EVENT_BUS.post(event);

		// Handle if the event is canceled
		if (event.isCanceled()) {
			// Let the client know the block still exists
			player.networkHandler.sendPacket(new BlockUpdateS2CPacket(world, pos));

			// Update any block entity data for this block
			BlockEntity entity = world.getBlockEntity(pos);

			if (entity != null) {
				BlockEntityUpdateS2CPacket packet = entity.toUpdatePacket();

				if (packet != null) {
					player.networkHandler.sendPacket(packet);
				}
			}

			return -1; // Cancelled
		} else {
			return event.getExpToDrop();
		}
	}

	// TODO: Leaving this unfired is intentional. See: https://github.com/MinecraftForge/MinecraftForge/issues/5828
	public static float fireBlockHarvesting(DefaultedList<ItemStack> drops, World world, BlockPos pos, BlockState state, int fortune, float dropChance, boolean silkTouch, PlayerEntity player) {
		BlockEvent.HarvestDropsEvent event = new BlockEvent.HarvestDropsEvent(world, pos, state, fortune, dropChance, drops, player, silkTouch);
		MinecraftForge.EVENT_BUS.post(event);
		return event.getDropChance();
	}

	public static void fireChunkWatch(boolean watch, ServerPlayerEntity entity, ChunkPos chunkpos, ServerWorld world) {
		if (watch) {
			MinecraftForge.EVENT_BUS.post(new ChunkWatchEvent.Watch(entity, chunkpos, world));
		} else {
			MinecraftForge.EVENT_BUS.post(new ChunkWatchEvent.UnWatch(entity, chunkpos, world));
		}
	}

	@Override
	public void onInitialize() {
		ServerWorldEvents.LOAD.register((server, world) -> {
			// Fabric fires this much earlier than Forge does for the overworld
			// So, we're going to manually fire it for the overworld.
			if (world.getDimension().getType() != DimensionType.OVERWORLD) {
				onWorldLoad(world);
			}
		});
	}
}
