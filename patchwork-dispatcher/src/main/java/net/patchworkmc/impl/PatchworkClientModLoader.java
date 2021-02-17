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

package net.patchworkmc.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.ClientBuiltinResourcePackProvider;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.profiler.Profiler;

public class PatchworkClientModLoader {
	private static final Logger LOGGER = LogManager.getLogger(PatchworkClientModLoader.class);
	private static boolean loading;
	private static MinecraftClient mc;

	public static void begin(final MinecraftClient minecraft, final ReloadableResourceManager mcResourceManager, ClientBuiltinResourcePackProvider metadataSerializer) {
		loading = true;
		PatchworkClientModLoader.mc = minecraft;
		Patchwork.gatherAndInitializeMods();
		mcResourceManager.registerListener(PatchworkClientModLoader::onReload);
	}

	/**
	 * @param syncExecutor The main thread executor
	 */
	private static CompletableFuture<Void> onReload(final ResourceReloadListener.Synchronizer stage, final ResourceManager resourceManager,
						final Profiler prepareProfiler, final Profiler executeProfiler, final Executor asyncExecutor, final Executor syncExecutor) {
		return CompletableFuture.runAsync(() -> startModLoading(syncExecutor), asyncExecutor)
				.thenCompose(stage::whenPrepared)
				.thenRunAsync(() -> finishModLoading(syncExecutor), asyncExecutor);
	}

	private static void startModLoading(Executor mainThreadExecutor) {
		LOGGER.debug("Patchwork Client Mod Loader: Start mod loading.");
		mainThreadExecutor.execute(() -> Patchwork.loadMods(container -> new FMLClientSetupEvent(() -> PatchworkClientModLoader.mc, container),
				PatchworkClientModLoader::preSidedRunnable, PatchworkClientModLoader::postSidedRunnable));
	}

	private static void preSidedRunnable(Consumer<Supplier<Event>> perModContainerEventProcessor) {
		//perModContainerEventProcessor.accept(ModelRegistryEvent::new);
	}

	private static void postSidedRunnable(Consumer<Supplier<Event>> perModContainerEventProcessor) {
	}

	private static void finishModLoading(Executor executor) {
		LOGGER.debug("Patchwork Client Mod Loader: Finish mod loading.");
		Patchwork.finishMods();
		loading = false;
		// reload game settings on main thread
		executor.execute(() -> mc.options.load());
	}

	/**
	 * @return true if an error occurred so that we can cancel the normal title screen.
	 */
	public static boolean completeModLoading() {
		LOGGER.debug("Patchwork Client Mod Loader: Complete mod loading");
		// Assume there's no error.
		MinecraftForge.EVENT_BUS.start();
		return false;
	}

	// TODO: Reserved for future use
	public static void onResourceReloadComplete(boolean errorFree) {
	}
}
