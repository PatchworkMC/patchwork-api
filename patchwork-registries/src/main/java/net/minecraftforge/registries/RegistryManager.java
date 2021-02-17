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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import net.patchworkmc.impl.registries.ForgeRegistryProvider;

//TODO: unimplemented features: saveToDisk, legacyName, sync, dump
public class RegistryManager {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final RegistryManager ACTIVE = new RegistryManager("ACTIVE");

	private final String name;
	// Class->Forge's identifier
	private BiMap<Class<? extends IForgeRegistryEntry<?>>, Identifier> superTypes = HashBiMap.create();
	// Forge's identifier->Vanilla identifier, e.g: activities->activity
	private Map<Identifier, Identifier> vanillaRegistryIds = new HashMap<>();

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

	/**
	 * @param key the forge or vanilla identifier of the registry
	 * @return Map forge registry name to vanilla, if such mapping exists
	 */
	public Identifier getVanillaRegistryId(Identifier key) {
		if (vanillaRegistryIds.containsKey(key)) {
			key = vanillaRegistryIds.get(key);
		}

		return key;
	}

	/**
	 * @param key the forge or vanilla identifier of the registry
	 * @return the ForgeRegistry instance, null if not found
	 */
	@SuppressWarnings("unchecked")
	public <V extends IForgeRegistryEntry<V>> ForgeRegistry<V> getRegistry(Identifier key) {
		// Patchwork: use a vanilla registry instead
		key = getVanillaRegistryId(key);
		Registry<?> vanillaRegistry = Registry.REGISTRIES.get(key);

		if (vanillaRegistry instanceof ForgeRegistryProvider) {
			return ((ForgeRegistryProvider) vanillaRegistry).patchwork$getForgeRegistry();
		}

		// TODO: should we make fabric registries visible to forge mods?
		return null;
	}

	public <V extends IForgeRegistryEntry<V>> ForgeRegistry<V> getRegistry(RegistryKey<? extends Registry<V>> key) {
		return getRegistry(key.getValue());
	}

	public <V extends IForgeRegistryEntry<V>> IForgeRegistry<V> getRegistry(Class<? super V> clazz) {
		// patchwork: use getName instead
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
		while (clazz != null && clazz != Object.class) {
			Identifier existingKey = RegistryManager.ACTIVE.superTypes.get(clazz);

			if (existingKey != null) {
				return existingKey;
			}

			clazz = clazz.getSuperclass();
		}

		return null;
	}

	public Set<Identifier> getRegistryNames() {
		// These types are known to MinecraftForge
		return this.superTypes.values();
	}

	/*package-private*/ <V extends IForgeRegistryEntry<V>> ForgeRegistry<V> createRegistry(Identifier forgeName, RegistryBuilder<V> builder) {
		Set<Class<?>> parents = new HashSet<>();
		findSuperTypes(builder.getType(), parents);
		SetView<Class<?>> overlappedTypes = Sets.intersection(parents, superTypes.keySet());

		if (!overlappedTypes.isEmpty()) {
			Class<?> foundType = overlappedTypes.iterator().next();
			LOGGER.error(
					"Found existing registry of type {} named {}, you cannot create a new registry ({}) with type {}, as {} has a parent of that type",
					foundType, superTypes.get(foundType), forgeName, builder.getType(), builder.getType());
			throw new IllegalArgumentException(
					"Duplicate registry parent type found - you can only have one registry for a particular super type");
		}

		ForgeRegistry<V> reg = new ForgeRegistry<>(this, forgeName, builder);
		superTypes.put(builder.getType(), forgeName);
		Identifier vanillaId = builder.patchwork$getVanillaRegistryKey().getValue();

		if (vanillaId != null && !vanillaId.equals(forgeName)) {
			vanillaRegistryIds.put(forgeName, vanillaId);
		}

		return reg;
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
}
