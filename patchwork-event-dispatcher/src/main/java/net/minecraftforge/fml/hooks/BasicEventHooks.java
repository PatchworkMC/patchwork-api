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

package net.minecraftforge.fml.hooks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import net.patchworkmc.impl.event.entity.EntityEvents;
import net.patchworkmc.impl.event.lifecycle.LifecycleEvents;

public class BasicEventHooks {
	// public static void firePlayerChangedDimensionEvent(PlayerEntity player, DimensionType fromDim, DimensionType toDim) {
	//
	// }

	public static void firePlayerLoggedIn(PlayerEntity player) {
		EntityEvents.onPlayerLoggedIn(player);
	}

	// public static void firePlayerLoggedOut(PlayerEntity player) {
	//
	// }

	// public static void firePlayerRespawnEvent(PlayerEntity player, boolean endConquered) {
	//
	// }

	// public static void firePlayerItemPickupEvent(PlayerEntity player, ItemEntity item, ItemStack clone) {
	//
	// }

	// public static void firePlayerCraftingEvent(PlayerEntity player, ItemStack crafted, Inventory craftMatrix) {
	//
	// }

	// public static void firePlayerSmeltedEvent(PlayerEntity player, ItemStack smelted) {
	//
	// }

	// public static void onRenderTickStart(float timer) {
	//
	// }

	// public static void onRenderTickEnd(float timer) {
	//
	// }

	public static void onPlayerPreTick(PlayerEntity player) {
		LifecycleEvents.onPlayerPreTick(player);
	}

	public static void onPlayerPostTick(PlayerEntity player) {
		LifecycleEvents.onPlayerPostTick(player);
	}

	public static void onPreWorldTick(World world) {
		LifecycleEvents.fireWorldTickEvent(TickEvent.Phase.START, world);
	}

	public static void onPostWorldTick(World world) {
		LifecycleEvents.fireWorldTickEvent(TickEvent.Phase.END, world);
	}

	// TODO: unlike almost everything else in this class, this doesn't delegate to a module specific method implementation.
	public static void onPreClientTick() {
		MinecraftForge.EVENT_BUS.post(new TickEvent.ClientTickEvent(TickEvent.Phase.START));
	}

	// TODO: unlike almost everything else in this class, this doesn't delegate to a module specific method implementation.
	public static void onPostClientTick() {
		MinecraftForge.EVENT_BUS.post(new TickEvent.ClientTickEvent(TickEvent.Phase.END));
	}

	// public static void onPreServerTick() {
	//
	// }

	// public static void onPostServerTick() {
	//
	// }
}
