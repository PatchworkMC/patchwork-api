package com.patchworkmc.impl.extension;

import net.minecraft.entity.Entity;

public interface PatchworkEntityTypeExtensions<T extends Entity> {
	// Patchwork: Forge does this through patching the constructor instead
	void setUpdateInterval(int interval);

	void setTrackingRange(int range);

	void setShouldReceiveVelocityUpdates(boolean value);

	// TODO: setCustomClientFactory
}
