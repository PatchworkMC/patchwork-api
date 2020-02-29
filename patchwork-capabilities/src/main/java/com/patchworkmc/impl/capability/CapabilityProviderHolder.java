/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2020, 2019-2020
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.patchworkmc.impl.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;

/**
 * A holder for {@link CapabilityProvider}, since some classes cannot directly extend {@link CapabilityProvider}.
 */
public interface CapabilityProviderHolder extends ICapabilityProvider {
	@Nonnull
	CapabilityProvider<?> getCapabilityProvider();

	default void gatherCapabilities() {
		getCapabilityProvider().gatherCapabilities();
	}

	default void gatherCapabilities(@Nullable ICapabilityProvider parent) {
		getCapabilityProvider().gatherCapabilities(parent);
	}

	default CapabilityDispatcher getCapabilities() {
		return getCapabilityProvider().getCapabilities();
	}

	default boolean areCapsCompatible(CapabilityProvider<?> other) {
		return getCapabilityProvider().areCapsCompatible((CapabilityProvider) other);
	}

	default boolean areCapsCompatible(CapabilityDispatcher other) {
		return getCapabilityProvider().areCapsCompatible(other);
	}

	@Nullable
	default CompoundTag serializeCaps() {
		return getCapabilityProvider().serializeCaps();
	}

	default void deserializeCaps(CompoundTag tag) {
		getCapabilityProvider().deserializeCaps(tag);
	}

	default void invalidateCaps() {
		getCapabilityProvider().invalidateCaps();
	}

	default void reviveCaps() {
		getCapabilityProvider().reviveCaps();
	}

	@Nonnull
	default <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		return getCapabilityProvider().getCapability(cap, side);
	}

	@Nonnull
	default <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
		return getCapabilityProvider().getCapability(cap);
	}
}
