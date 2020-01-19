package com.patchworkmc.impl.patches.bugfixes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

public interface EntityTypeBuilderShim<T extends Entity> {
	EntityType<T> build(String id);
}
