package com.patchworkmc.impl.extension;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

public interface PatchworkEntityTypeBuilderExtensions<T extends Entity> {
	EntityType.Builder<T> setUpdateInterval(int interval);

	EntityType.Builder<T> setTrackingRange(int range);

	EntityType.Builder<T> setShouldReceiveVelocityUpdates(boolean value);

	// TODO: setCustomClientFactory
}
