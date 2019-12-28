package net.minecraftforge.fml;

import java.util.List;

public class ModList {
	private static ModList INSTANCE;
	private List<String> mods;
	//Patchwork: signature changed to just have a list of modids
	private ModList(List<String> mods) {
		this.mods = mods;
	}
	public static ModList get() {
		return INSTANCE;
	}
	//Patchwork: method does not exist in Forge
	public static ModList create(List<String> mods) {
		INSTANCE = new ModList(mods);
		return INSTANCE;
	}
	
	public boolean isLoaded(String modId) {
		return this.mods.contains(modId);
	}

}
