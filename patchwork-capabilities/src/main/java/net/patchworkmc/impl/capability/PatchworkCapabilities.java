package net.patchworkmc.impl.capability;

import net.fabricmc.api.ModInitializer;
import net.minecraftforge.energy.CapabilityEnergy;

public class PatchworkCapabilities implements ModInitializer {
	@Override
	public void onInitialize() {
		CapabilityEnergy.register();
	}
}
