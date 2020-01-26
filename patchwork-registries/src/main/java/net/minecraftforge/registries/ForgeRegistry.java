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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;

public class ForgeRegistry<V extends IForgeRegistryEntry<V>> implements IForgeRegistry<V> {
	public static Marker REGISTRIES = MarkerManager.getMarker("REGISTRIES");
	private static Logger LOGGER = LogManager.getLogger();
	private Identifier name;
	private Registry<V> vanilla;
	private Class<V> superType;

	public ForgeRegistry(Identifier name, Registry<V> vanilla, Class<V> superType) {
		this.name = name;
		this.vanilla = vanilla;
		this.superType = superType;
	}

	@Override
	public Identifier getRegistryName() {
		return name;
	}

	@Override
	public Class<V> getRegistrySuperType() {
		return superType;
	}

	@Override
	public void register(V value) {
		Objects.requireNonNull(value, "value must not be null");
		Identifier identifier = value.getRegistryName();

		Optional<V> potentialOldValue = vanilla.getOrEmpty(identifier);

		potentialOldValue.ifPresent(
				oldValue -> {
					if (oldValue == value) {
						LOGGER.warn(REGISTRIES, "Registry {}: The object {} has been registered twice for the same name {}.", this.superType.getSimpleName(), value, identifier);

						return;
					} else {
						throw new IllegalArgumentException(String.format("The name %s has been registered twice, for %s and %s.", identifier, oldValue, value));
					}
				}
		);

		Identifier oldIdentifier = vanilla.getId(value);

		if (oldIdentifier != getDefaultKey()) {
			throw new IllegalArgumentException(String.format("The object %s{%x} has been registered twice, using the names %s and %s.", value, System.identityHashCode(value), oldIdentifier, identifier));
		}

		Registry.register(vanilla, identifier, value);
	}

	@Override
	public void registerAll(V... values) {
		for (V value : values) {
			register(value);
		}
	}

	@Override
	public boolean containsKey(Identifier key) {
		return vanilla.containsId(key);
	}

	@Override
	public boolean containsValue(V value) {
		return vanilla.getId(value) != null;
	}

	@Override
	public boolean isEmpty() {
		return vanilla.getIds().isEmpty();
	}

	@Override
	public V getValue(Identifier key) {
		return vanilla.get(key);
	}

	@Override
	public Identifier getKey(V value) {
		return vanilla.getId(value);
	}

	@Override
	public Identifier getDefaultKey() {
		if (vanilla instanceof DefaultedRegistry) {
			return ((DefaultedRegistry<V>) vanilla).getDefaultId();
		}

		return null;
	}

	@Override
	public Set<Identifier> getKeys() {
		return vanilla.getIds();
	}

	@Override
	public Collection<V> getValues() {
		return vanilla.stream().collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public Set<Map.Entry<Identifier, V>> getEntries() {
		HashSet<Map.Entry<Identifier, V>> entries = new HashSet<>();

		for (Identifier identifier : vanilla.getIds()) {
			entries.add(new Entry<>(identifier, vanilla.get(identifier)));
		}

		return entries;
	}

	@Override
	@Nonnull
	public Iterator<V> iterator() {
		return vanilla.stream().iterator();
	}

	private static class Entry<V> implements Map.Entry<Identifier, V> {
		private Identifier identifier;
		private V value;

		private Entry(Identifier identifier, V value) {
			this.identifier = identifier;
			this.value = value;
		}

		@Override
		public Identifier getKey() {
			return identifier;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			throw new UnsupportedOperationException("Cannot update a registry entry in place yet!");
		}

		@Override
		@SuppressWarnings("rawtypes")
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}

			if (!(o instanceof Entry)) {
				return false;
			}

			Entry e = (Entry) o;

			return identifier.equals(e.identifier) && value.equals(e.value);
		}

		@Override
		public int hashCode() {
			return identifier.hashCode() * 33 + value.hashCode();
		}
	}
}
