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

package com.patchworkmc.impl.registries;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;

@ParametersAreNonnullByDefault
public class Identifiers {
	private Identifiers() {
	}

	/**
	 * Gets the current {@link Identifier} for the object in the registry if it exists. If the object does not exist in
	 * the provided registry, {@code fallback} will be returned instead.
	 *
	 * @param registry the registry to query. Must NOT be an instance of {@link DefaultedRegistry}, or else this method
	 *                 will throw an {@link IllegalArgumentException}.
	 * @return an {@link Identifier} if the instance is registered or if the fallback is non null, otherwise null
	 */
	@Nullable
	public static <T> Identifier getOrFallback(Registry<T> registry, T instance, @Nullable Identifier fallback) {
		if (registry instanceof DefaultedRegistry) {
			// While we could just cast here, I want to catch these cases where they come up and fix them in the mixin.
			throw new IllegalArgumentException("Used the non-defaulted getOrFallback method with a DefaultedRegistry");
		}

		Identifier current = registry.getId(instance);

		if (current == null) {
			return fallback;
		} else {
			return current;
		}
	}

	/**
	 * Gets the current {@link Identifier} for the object in the registry if it exists. If the object does not exist in
	 * the provided registry, {@code fallback} will be returned instead.
	 *
	 * @param registry the registry to query. Must be an instance of {@link DefaultedRegistry}.
	 * @return an {@link Identifier} if the instance is registered or if the fallback is non null, otherwise null
	 */
	@Nullable
	public static <T> Identifier getOrFallback(DefaultedRegistry<T> registry, T instance, @Nullable Identifier fallback) {
		Identifier current = registry.getId(instance);

		if (current.equals(registry.getDefaultId())) {
			return fallback;
		} else {
			return current;
		}
	}
}
