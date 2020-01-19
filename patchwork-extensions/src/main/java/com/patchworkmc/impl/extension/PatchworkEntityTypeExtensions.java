package com.patchworkmc.impl.extension;

import net.minecraft.entity.Entity;

public interface PatchworkEntityTypeExtensions<T extends Entity> {
	// Patchwork: Forge does this through patching the constructor instead
	public void setUpdateInterval(int interval);

	public void setTrackingRange(int range);

	public void setShouldReceiveVelocityUpdates(boolean value);
	// TODO: setCustomClientFactory

}
