package net.coderbot.patchwork;

public interface ForgeInitializer {
	String getModId();
	void onForgeInitialize();
}
