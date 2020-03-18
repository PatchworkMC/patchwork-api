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

package net.patchworkmc.impl.networking;

import java.util.concurrent.CompletableFuture;

import net.minecraft.util.thread.ThreadExecutor;

import net.patchworkmc.mixin.networking.accessor.ThreadExecutorAccessor;

public class PatchworkNetworking {
	private static MessageFactory factory;
	private static NetworkVersionManager versionManager = new NetworkVersionManager();

	public static CompletableFuture<Void> enqueueWork(ThreadExecutor<?> executor, Runnable runnable) {
		// Must check ourselves as Minecraft will sometimes delay tasks even when they are received on the client thread
		// Same logic as ThreadTaskExecutor#runImmediately without the join
		if (!executor.isOnThread()) {
			// Use the internal method so thread check isn't done twice
			return ((ThreadExecutorAccessor) executor).patchwork$submitAsync(runnable);
		} else {
			runnable.run();
			return CompletableFuture.completedFuture(null);
		}
	}

	public static void setFactory(MessageFactory factory) {
		PatchworkNetworking.factory = factory;
	}

	public static MessageFactory getMessageFactory() {
		return factory;
	}

	public static NetworkVersionManager getVersionManager() {
		return versionManager;
	}
}
