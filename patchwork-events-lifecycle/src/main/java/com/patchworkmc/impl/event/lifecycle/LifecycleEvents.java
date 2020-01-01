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

package com.patchworkmc.impl.event.lifecycle;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.event.world.WorldTickCallback;

public class LifecycleEvents implements ModInitializer {
	public static void fireWorldTickEvent(TickEvent.Phase phase, World world) {
		LogicalSide side = world.isClient() ? LogicalSide.CLIENT : LogicalSide.SERVER;
		TickEvent.WorldTickEvent event = new TickEvent.WorldTickEvent(side, phase, world);

		MinecraftForge.EVENT_BUS.post(event);
	}

	public static void onPlayerPreTick(PlayerEntity player) {
		MinecraftForge.EVENT_BUS.post(new TickEvent.PlayerTickEvent(TickEvent.Phase.START, player));
	}

	public static void onPlayerPostTick(PlayerEntity player) {
		MinecraftForge.EVENT_BUS.post(new TickEvent.PlayerTickEvent(TickEvent.Phase.END, player));
	}

	public static void handleServerStarting(final MinecraftServer server) {
		// TODO: Forge loads language data here. I haven't found any mods that use this behavior.

		if (MinecraftForge.EVENT_BUS.post(new FMLServerStartingEvent(server))) {
			throw new UnsupportedOperationException("FMLServerStartingEvent is not cancellable!");
		}
	}

	public static void handleServerStarted(final MinecraftServer server) {
		MinecraftForge.EVENT_BUS.post(new FMLServerStartedEvent(server));
	}

	@Override
	public void onInitialize() {
		WorldTickCallback.EVENT.register(world -> fireWorldTickEvent(TickEvent.Phase.END, world));

		ServerStartCallback.EVENT.register(server -> {
			String motdBefore = server.getServerMotd();
			handleServerStarted(server);
			String motdAfter = server.getServerMotd();

			if (!motdBefore.equals(motdAfter)) {
				// TODO: If a mod tries to set the server's motd field here, behavior may differ.
				// I'm leaving this here because I haven't found any mods that actually do this.

				throw new UnsupportedOperationException("A mod tried to set the server's motd during handling FMLServerStartedEvent, this isn't implemented yet.");
			}
		});
	}
}
