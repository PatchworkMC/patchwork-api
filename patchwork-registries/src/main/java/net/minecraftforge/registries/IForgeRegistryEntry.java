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

package net.minecraftforge.registries;

import net.minecraft.util.Identifier;

public interface IForgeRegistryEntry<V> {
	/**
	 * A unique {@link Identifier} for this entry, if this entry is registered already it will return it's official {@link Identifier}.
	 * Otherwise it will return the name set in {@link #setRegistryName(Identifier)}. If neither are valid {@code null} is returned.
	 *
	 * @return the unique {@link Identifier} or {@code null}.
	 */
	Identifier getRegistryName();

	/**
	 * The supplied {@link Identifier} will be prefixed with the currently active mod's modId.
	 * If the supplied {@link Identifier} already has a prefix that is different, it will be used and a warning will be logged.
	 *
	 * <p>If a name already exists, or this Item is already registered in a registry, then an IllegalStateException is thrown.</p>
	 *
	 * @param name the unique {@link Identifier}
	 * @return this instance, to allow for chaining
	 */
	V setRegistryName(Identifier name);

	/**
	 * Determines the type for this entry, used to look up the correct registry in the global registries list as there can only be one
	 * {@link net.minecraft.util.registry.Registry} per concrete {@link Class}.
	 *
	 * @return the root registry type
	 */
	Class<V> getRegistryType();
}
