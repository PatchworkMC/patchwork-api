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

package net.patchworkmc.impl.event.lifecycle;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.loading.FileUtils;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.event.world.WorldTickCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class LifecycleEvents implements ModInitializer {
	public static void fireWorldTickEvent(TickEvent.Phase phase, World world) {
		LogicalSide side = world.isClient() ? LogicalSide.CLIENT : LogicalSide.SERVER;
		TickEvent.WorldTickEvent event = new TickEvent.WorldTickEvent(side, phase, world);

		MinecraftForge.EVENT_BUS.post(event);
	}

	public static void fireClientTickEvent(TickEvent.Phase phase) {
		MinecraftForge.EVENT_BUS.post(new TickEvent.ClientTickEvent(phase));
	}

	public static void fireRenderTickEvent(TickEvent.Phase phase, float renderTickTime) {
		// TODO - Call net.minecraftforge.client.model.animation.Animation#setClientPartialTickTime on start phase
		MinecraftForge.EVENT_BUS.post(new TickEvent.RenderTickEvent(phase, renderTickTime));
	}

	public static void fireServerTickEvent(TickEvent.Phase phase) {
		MinecraftForge.EVENT_BUS.post(new TickEvent.ServerTickEvent(phase));
	}

	public static void firePlayerTickEvent(TickEvent.Phase phase, PlayerEntity player) {
		MinecraftForge.EVENT_BUS.post(new TickEvent.PlayerTickEvent(phase, player));
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

	public static void handleServerAboutToStart(final MinecraftServer server) {
		ServerLifecycleHooks.currentServer = server;
		LogicalSidedProvider.setServer(() -> server);
		ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER, getServerConfigPath(server));
		// TODO: ResourcePackLoader.loadResourcePacks(currentServer.getDataPackManager(), ServerLifecycleHooks::buildPackFinder);
		MinecraftForge.EVENT_BUS.post(new FMLServerAboutToStartEvent(server));
	}

	private static Path getServerConfigPath(final MinecraftServer server) {
		final Path serverConfig = server.getLevelStorage().resolveFile(server.getLevelName(), "serverconfig").toPath();
		FileUtils.getOrCreateDirectory(serverConfig, "serverconfig");
		return serverConfig;
	}

	public static void handleServerStopped(final MinecraftServer server) {
		MinecraftForge.EVENT_BUS.post(new FMLServerStoppedEvent(server));
		ServerLifecycleHooks.currentServer = null;
		LogicalSidedProvider.setServer(null);
		CountDownLatch latch = ServerLifecycleHooks.exitLatch;

		if (latch != null) {
			latch.countDown();
			ServerLifecycleHooks.exitLatch = null;
		}
	}

	@Override
	public void onInitialize() {
		WorldTickCallback.EVENT.register(world -> fireWorldTickEvent(TickEvent.Phase.END, world));

		ServerStartCallback.EVENT.register(server -> {
			String motdBefore = server.getServerMotd();
			handleServerStarted(server);
			String motdAfter = server.getServerMotd();

			if (!motdBefore.equals(motdAfter)) {
				// TODO: If a mod tries to set the server's motd field here, behavior may differ,
				// because of differences between where ServerStartCallback and FMLServerStartedEvent are fired.
				// Technically, if a mod set the motd in the event on Forge, it would become part of the metadata
				// but, ServerStartCallback happens a few calls after that, once the motd has already been set in the metadata
				// I'm leaving this here because I haven't found any mods that actually do this.

				throw new UnsupportedOperationException("A mod tried to set the server's motd during handling FMLServerStartedEvent, this isn't implemented yet.");
			}
		});

		ServerTickEvents.START_SERVER_TICK.register(server -> fireServerTickEvent(TickEvent.Phase.START));
		ServerTickEvents.END_SERVER_TICK.register(server -> fireServerTickEvent(TickEvent.Phase.END));
	}
}
