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

import net.minecraftforge.event.TickEvent;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import net.patchworkmc.impl.event.entity.PlayerEvents;
import net.patchworkmc.impl.event.lifecycle.LifecycleEvents;

/*
 * Note: this class is intended for mod use only, to dispatch to the implementations kept in their own modules.
 * Do not keep implementation details here, methods should be thin wrappers around methods in other modules.
 */
public class BasicEventHooks {
	public static void firePlayerLoggedIn(PlayerEntity player) {
		PlayerEvents.firePlayerLoggedIn(player);
	}

	public static void onPlayerPreTick(PlayerEntity player) {
		LifecycleEvents.firePlayerTickEvent(TickEvent.Phase.START, player);
	}

	public static void onPlayerPostTick(PlayerEntity player) {
		LifecycleEvents.firePlayerTickEvent(TickEvent.Phase.END, player);
	}

	public static void onPreWorldTick(World world) {
		LifecycleEvents.fireWorldTickEvent(TickEvent.Phase.START, world);
	}

	public static void onPostWorldTick(World world) {
		LifecycleEvents.fireWorldTickEvent(TickEvent.Phase.END, world);
	}

	public static void onPreClientTick() {
		LifecycleEvents.fireClientTickEvent(TickEvent.Phase.START);
	}

	public static void onPostClientTick() {
		LifecycleEvents.fireClientTickEvent(TickEvent.Phase.END);
	}
}
