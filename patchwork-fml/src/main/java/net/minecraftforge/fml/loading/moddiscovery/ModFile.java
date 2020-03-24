package net.minecraftforge.fml.loading.moddiscovery;

import net.minecraftforge.forgespi.language.ModFileScanData;

public class ModFile {
	private String modid;
	private ModFileScanData modFileScanData;

	public ModFile(String modid) {
		this.modid = modid;
		modFileScanData = new ModFileScanData(modid);
	}

	public ModFileScanData getScanResult() {
		return modFileScanData;
	}
}
