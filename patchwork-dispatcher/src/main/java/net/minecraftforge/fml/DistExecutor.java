package net.minecraftforge.fml;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public final class DistExecutor {
	private DistExecutor() {
	}

	/**
	 * Run the callable in the supplier only on the specified {@link Dist}
	 *
	 * @param dist  The dist to run on
	 * @param toRun A supplier of the callable to run (Supplier wrapper to ensure classloading only on the appropriate dist)
	 * @param <T>   The return type from the callable
	 * @return The callable's result
	 */
	public static <T> T callWhenOn(Dist dist, Supplier<Callable<T>> toRun) {
		if (dist == FMLEnvironment.dist) {
			try {
				return toRun.get().call();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	public static void runWhenOn(Dist dist, Supplier<Runnable> toRun) {
		if (dist == FMLEnvironment.dist) {
			toRun.get().run();
		}
	}

	public static <T> T runForDist(Supplier<Supplier<T>> clientTarget, Supplier<Supplier<T>> serverTarget) {
		switch (FabricLoader.getInstance().getEnvironmentType()) {
			case CLIENT:
				return clientTarget.get().get();
			case SERVER:
				return serverTarget.get().get();
			default:
				throw new IllegalArgumentException("UNSIDED?");
		}
	}
}