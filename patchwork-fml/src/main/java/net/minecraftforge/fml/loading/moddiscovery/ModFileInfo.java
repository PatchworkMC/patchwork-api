package net.minecraftforge.fml.loading.moddiscovery;

public class ModFileInfo {
	private ModFile modFile;

	public ModFileInfo(String modid) {
		modFile = new ModFile(modid);
	}

	public ModFile getFile() {
		return modFile;
	}
}
