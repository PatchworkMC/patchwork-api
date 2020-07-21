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

package net.minecraftforge.fml.server;

import java.util.concurrent.CountDownLatch;

import net.minecraft.server.MinecraftServer;

import net.patchworkmc.impl.event.lifecycle.LifecycleEvents;

/**
 * This is a stub of the ServerLifecycleHooks class in Forge for mods that use getCurrentServer.
 */
public class ServerLifecycleHooks {
	public static MinecraftServer currentServer;
	public static volatile CountDownLatch exitLatch = null;

	public static MinecraftServer getCurrentServer() {
		return currentServer;
	}

	// Forge returns `!MinecraftForge.EVENT_BUS.post(...)`, so true == continue, false == cancel.
	public static boolean handleServerAboutToStart(final MinecraftServer server) {
		LifecycleEvents.handleServerAboutToStart(server);
		return true; // patchwork does not allow you to cancel server startup.
	}

	// Forge returns `!MinecraftForge.EVENT_BUS.post(...)`, so true == continue, false == cancel.
	public static boolean handleServerStarting(final MinecraftServer server) {
		LifecycleEvents.handleServerStarting(server);
		return true; // patchwork does not allow you to cancel server startup.
	}

	public static void handleServerStarted(final MinecraftServer server) {
		LifecycleEvents.handleServerStarted(server);
	}

	public static void handleServerStopped(final MinecraftServer server) {
		LifecycleEvents.handleServerStopped(server);
	}
}
