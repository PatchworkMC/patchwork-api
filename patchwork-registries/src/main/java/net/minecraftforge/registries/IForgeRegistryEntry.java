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

package net.minecraftforge.registries;

import net.minecraft.util.Identifier;

public interface IForgeRegistryEntry<V> {
	/**
	 * A unique identifier for this entry, if this entry is registered already it will return it's official registry name.
	 * Otherwise it will return the name set in setRegistryName().
	 * If neither are valid null is returned.
	 *
	 * @return Unique identifier or null.
	 */
	Identifier getRegistryName();

	/**
	 * The supplied name will be prefixed with the currently active mod's modId.
	 * If the supplied name already has a prefix that is different, it will be used and a warning will be logged.
	 * <p>
	 * If a name already exists, or this Item is already registered in a registry, then an IllegalStateException is thrown.
	 * <p>
	 * Returns 'this' to allow for chaining.
	 *
	 * @param name Unique registry name
	 * @return This instance
	 */
	IForgeRegistryEntry setRegistryName(Identifier name);

	/**
	 * Determines the type for this entry, used to look up the correct registry in the global registries list as there can only be one
	 * registry per concrete class.
	 *
	 * @return Root registry type.
	 */
	Class<V> getRegistryType();
}
