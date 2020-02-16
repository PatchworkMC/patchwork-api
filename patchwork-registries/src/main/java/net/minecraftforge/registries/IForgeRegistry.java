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

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.util.Identifier;

/**
 * Main interface for the registry system. Use this to query the registry system.
 *
 * @param <V> The top level type for the registry
 */
public interface IForgeRegistry<V extends IForgeRegistryEntry<V>> extends Iterable<V> {
	Identifier getRegistryName();

	Class<V> getRegistrySuperType();

	void register(V value);

	void registerAll(@SuppressWarnings("unchecked") V... values);

	boolean containsKey(Identifier key);

	boolean containsValue(V value);

	boolean isEmpty();

	V getValue(Identifier key);

	Identifier getKey(V value);

	Identifier getDefaultKey();

	Set<Identifier> getKeys();

	Collection<V> getValues();

	Set<Entry<Identifier, V>> getEntries();
}
