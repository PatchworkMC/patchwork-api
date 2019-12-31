/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2019, 2019
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
 * Accepts redirects to all of {@link CapabilityProvider CapabilityProvider's} public methods.
 */
public interface CapabilityProxy {
	/**
	 * Internal use only!
	 */
	static CapabilityProvider<?> getProvider(Object object) {
		if (object == null) {
			return null;
		}

		if (object instanceof CapabilityProviderHolder) {
			return ((CapabilityProviderHolder) object).getCapabilityProvider();
		}

		return (CapabilityProvider<?>) object;
	}

	static void gatherCapabilities(Object provider) {
		getProvider(provider).gatherCapabilities();
	}

	static void gatherCapabilities(Object provider, @Nullable ICapabilityProvider parent) {
		getProvider(provider).gatherCapabilities(parent);
	}

	static CapabilityDispatcher getCapabilities(Object provider) {
		return getProvider(provider).getCapabilities();
	}

	static boolean areCapsCompatible(Object provider, CapabilityProvider<?> other) {
		return getProvider(provider).areCapsCompatible((CapabilityProvider) other);
	}

	static boolean areCapsCompatible(Object provider, CapabilityDispatcher other) {
		return getProvider(provider).areCapsCompatible(other);
	}

	@Nullable
	static CompoundTag serializeCaps(Object provider) {
		return getProvider(provider).serializeCaps();
	}

	static void deserializeCaps(Object provider, CompoundTag tag) {
		getProvider(provider).deserializeCaps(tag);
	}

	static void invalidateCaps(Object provider) {
		getProvider(provider).invalidateCaps();
	}

	static void reviveCaps(Object provider) {
		getProvider(provider).reviveCaps();
	}

	@Nonnull
	static <T> LazyOptional<T> getCapability(Object provider, @Nonnull Capability<T> cap, @Nullable Direction side) {
		return getProvider(provider).getCapability(cap, side);
	}

	@Nonnull
	static <T> LazyOptional<T> getCapability(Object provider, @Nonnull Capability<T> cap) {
		return getProvider(provider).getCapability(cap);
	}
}
