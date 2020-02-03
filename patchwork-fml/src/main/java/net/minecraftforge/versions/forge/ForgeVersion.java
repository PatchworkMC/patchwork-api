package net.minecraftforge.versions.forge;

import javax.annotation.Nullable;

/**
 * Dummy class for forge version.
 */
public class ForgeVersion {
	public static final String MOD_ID = "forge";

	public static String getVersion() {
		return "28.1";
	}

	@Nullable
	public static String getTarget() {
		return "";
	}

	public static String getSpec() {
		return "28.1";
	}

	public static String getGroup() {
		return "net.minecraftforge";
	}
}
