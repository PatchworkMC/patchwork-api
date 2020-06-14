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

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.patchworkmc.impl.registries.ForgeRegistryProvider;

// TODO: unimplemented features: saveToDisk, legacyName, sync, dump
public class RegistryManager {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final RegistryManager ACTIVE = new RegistryManager("ACTIVE");

	private final String name;
	private BiMap<Class<? extends IForgeRegistryEntry<?>>, Identifier> superTypes = HashBiMap.create();

	private RegistryManager(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	@SuppressWarnings("unchecked")
	public <V extends IForgeRegistryEntry<V>> Class<V> getSuperType(Identifier key) {
		return (Class<V>) this.superTypes.inverse().get(key);
	}

	@SuppressWarnings("unchecked")
	public <V extends IForgeRegistryEntry<V>> ForgeRegistry<V> getRegistry(Identifier key) {
		Registry<?> vanillaRegistry = Registry.REGISTRIES.get(key);

		if (vanillaRegistry instanceof ForgeRegistryProvider) {
			return ((ForgeRegistryProvider) vanillaRegistry).getForgeRegistry();
		}

		return null;
	}

	public <V extends IForgeRegistryEntry<V>> IForgeRegistry<V> getRegistry(Class<? super V> clazz) {
		Identifier existingKey = this.getName(clazz);

		if (existingKey == null) {
			return null;
		}

		return getRegistry(existingKey);
	}

	public <V extends IForgeRegistryEntry<V>> Identifier getName(IForgeRegistry<V> reg) {
		return reg.getRegistryName();
	}

	public Identifier getName(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}

		do {
			Identifier existingKey = RegistryManager.ACTIVE.superTypes.get(clazz);

			if (existingKey != null) {
				return existingKey;
			}

			clazz = clazz.getSuperclass();
		} while (clazz != null && clazz != Object.class);

		return null;
	}

	public Set<Identifier> getRegistryNames() {
		// These types are known to MinecraftForge
		return this.superTypes.values();
	}

	<V extends IForgeRegistryEntry<V>> ForgeRegistry<V> createRegistry(Identifier name, RegistryBuilder<V> builder) {
		Set<Class<?>> parents = Sets.newHashSet();
		findSuperTypes(builder.getType(), parents);
		SetView<Class<?>> overlappedTypes = Sets.intersection(parents, superTypes.keySet());

		if (!overlappedTypes.isEmpty()) {
			Class<?> foundType = overlappedTypes.iterator().next();
			throwSuperClassOverlapException(foundType, builder.getType());
		}

		ForgeRegistry<V> reg = new ForgeRegistry<V>(this, name, builder);
		superTypes.put(builder.getType(), name);

		return getRegistry(name);
	}

	private void findSuperTypes(Class<?> type, Set<Class<?>> types) {
		if (type == null || type == Object.class) {
			return;
		}

		types.add(type);

		for (Class<?> interfac : type.getInterfaces()) {
			findSuperTypes(interfac, types);
		}

		findSuperTypes(type.getSuperclass(), types);
	}

	private void throwSuperClassOverlapException(Class<?> foundType, Class<?> superClazz) {
		LOGGER.error(
				"Found existing registry of type {} named {}, you cannot create a new registry ({}) with type {}, as {} has a parent of that type",
				foundType, superTypes.get(foundType), name, superClazz, superClazz);
		throw new IllegalArgumentException(
				"Duplicate registry parent type found - you can only have one registry for a particular super type");
	}
}
