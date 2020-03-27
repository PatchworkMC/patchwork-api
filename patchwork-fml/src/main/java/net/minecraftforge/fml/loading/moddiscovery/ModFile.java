package net.minecraftforge.fml.loading.moddiscovery;

import net.minecraftforge.forgespi.language.ModFileScanData;

public class ModFile {
	private ModFileScanData modFileScanData;

	public ModFile(String modid) {
		modFileScanData = new ModFileScanData(modid);
	}

	public ModFileScanData getScanResult() {
		return modFileScanData;
	}
}
