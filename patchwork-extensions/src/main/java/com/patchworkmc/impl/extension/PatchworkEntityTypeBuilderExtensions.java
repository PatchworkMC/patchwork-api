package com.patchworkmc.impl.extension;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

public interface PatchworkEntityTypeBuilderExtensions<T extends Entity> {
	public EntityType.Builder<T> setUpdateInterval(int interval);

	public EntityType.Builder<T> setTrackingRange(int range);

	public EntityType.Builder<T> setShouldReceiveVelocityUpdates(boolean value);
	// TODO: setCustomClientFactory
}
