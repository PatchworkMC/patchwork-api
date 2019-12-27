package net.minecraftforge.fml.loading;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraftforge.api.distmarker.Dist;

public class FMLEnvironment {
	public static final Dist dist = Dist.fromEnvType(FabricLoader.getInstance().getEnvironmentType());

	// TODO: String naming, setupInteropEnvironment
}
