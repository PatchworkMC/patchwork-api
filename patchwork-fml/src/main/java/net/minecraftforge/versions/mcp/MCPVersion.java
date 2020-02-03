package net.minecraftforge.versions.mcp;

import net.minecraft.SharedConstants;

public class MCPVersion {
	public static String getMCVersion() {
		return SharedConstants.getGameVersion().getName();
	}

	/**
	 * Trust us! This is the correct mcp version! Please fall for it forge mods!
	 */
	public static String getMCPVersion() {
		return "20190829.143755";
	}

	public static String getMCPandMCVersion() {
		return getMCVersion() + "-" + getMCPVersion();
	}
}
