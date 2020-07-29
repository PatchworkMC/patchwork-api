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

package net.patchworkmc.impl.event.entity;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.dimension.DimensionType;

public class PlayerEvents {
	public static void firePlayerChangedDimensionEvent(PlayerEntity player, DimensionType fromDim, DimensionType toDim) {
		MinecraftForge.EVENT_BUS.post(new PlayerEvent.PlayerChangedDimensionEvent(player, fromDim, toDim));
	}

	public static void firePlayerLoggedIn(PlayerEntity player) {
		MinecraftForge.EVENT_BUS.post(new PlayerEvent.PlayerLoggedInEvent(player));
	}

	public static void firePlayerLoggedOut(PlayerEntity player) {
		MinecraftForge.EVENT_BUS.post(new PlayerEvent.PlayerLoggedOutEvent(player));
	}

	public static void firePlayerRespawnEvent(PlayerEntity player, boolean alive) {
		MinecraftForge.EVENT_BUS.post(new PlayerEvent.PlayerRespawnEvent(player, alive));
	}

	public static void firePlayerItemPickupEvent(PlayerEntity player, ItemEntity item, ItemStack clone) {
		MinecraftForge.EVENT_BUS.post(new PlayerEvent.ItemPickupEvent(player, item, clone));
	}

	public static void firePlayerCraftingEvent(PlayerEntity player, ItemStack crafted, Inventory craftMatrix) {
		MinecraftForge.EVENT_BUS.post(new PlayerEvent.ItemCraftedEvent(player, crafted, craftMatrix));
	}

	public static void firePlayerSmeltedEvent(PlayerEntity player, ItemStack smelted) {
		MinecraftForge.EVENT_BUS.post(new PlayerEvent.ItemSmeltedEvent(player, smelted));
	}
}
