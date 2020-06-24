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

package net.minecraftforge.fml;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * A dummy version of DeferredWorkQueue which instantly completes futures on the same thread that calls it.
 */
public class DeferredWorkQueue {
	public static CompletableFuture<Void> runLater(Runnable workToEnqueue) {
		workToEnqueue.run();
		return CompletableFuture.completedFuture(null);
	}

	public static CompletableFuture<Void> runLaterChecked(CheckedRunnable workToEnqueue) {
		CompletableFuture<Void> future = new CompletableFuture<>();

		try {
			workToEnqueue.run();
			future.complete(null);
		} catch (Throwable t) {
			future.completeExceptionally(t);
		}

		return future;
	}

	public static <T> CompletableFuture<T> getLater(Supplier<T> workToEnqueue) {
		return CompletableFuture.completedFuture(workToEnqueue.get());
	}

	public static <T> CompletableFuture<T> getLaterChecked(Callable<T> workToEnqueue) {
		CompletableFuture<T> future = new CompletableFuture<>();

		try {
			future.complete(workToEnqueue.call());
		} catch (Throwable t) {
			future.completeExceptionally(t);
		}

		return future;
	}

	@FunctionalInterface
	public interface CheckedRunnable {
		void run() throws Exception;
	}
}
