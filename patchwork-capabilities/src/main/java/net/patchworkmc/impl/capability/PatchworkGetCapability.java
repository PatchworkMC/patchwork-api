package net.patchworkmc.impl.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import net.minecraft.util.math.Direction;

public interface PatchworkGetCapability {
	default <T> LazyOptional<T> patchwork$getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		return null;
	};

	default <T> LazyOptional<T> patchwork$getCapability(@Nonnull Capability<T> cap) {
		return patchwork$getCapability(cap, null);
	};
}
