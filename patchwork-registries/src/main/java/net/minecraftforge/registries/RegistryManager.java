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

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.Identifier;

import net.patchworkmc.impl.registries.RegistryClassMapping;

public class RegistryManager {
	public static final RegistryManager ACTIVE = new RegistryManager("ACTIVE");

	private final String name;
	private final Map<Identifier, ForgeRegistry> registries;

	public RegistryManager(String name) {
		this.name = name;
		this.registries = new HashMap<>();
	}

	public String getName() {
		return this.name;
	}

	@SuppressWarnings("unchecked")
	public <V extends IForgeRegistryEntry<V>> Class<V> getSuperType(Identifier key) {
		return (Class<V>) RegistryClassMapping.getClass(key);
	}

	@SuppressWarnings("unchecked")
	public <V extends IForgeRegistryEntry<V>> ForgeRegistry<V> getRegistry(Identifier key) {
		return (ForgeRegistry<V>) this.registries.get(key);
	}

	public <V extends IForgeRegistryEntry<V>> IForgeRegistry<V> getRegistry(Class<? super V> clazz) {
		return getRegistry(RegistryClassMapping.getIdentifier(clazz));
	}

	public <V extends IForgeRegistryEntry<V>> Identifier getName(IForgeRegistry<V> reg) {
		return reg.getRegistryName();
	}

	/**
	 * Used by {@link net.minecraftforge.registries.ForgeRegistries}.
	 *
	 * @param registry the registry to add to the mapping
	 */
	void addRegistry(Identifier key, ForgeRegistry registry) {
		registries.put(key, registry);
	}
}
