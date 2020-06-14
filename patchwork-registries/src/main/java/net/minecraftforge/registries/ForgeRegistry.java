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
import java.util.HashMap;
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
import net.minecraft.util.registry.MutableRegistry;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;

import net.patchworkmc.impl.registries.ExtendedVanillaRegistry;
import net.patchworkmc.impl.registries.VanillaRegistry;

public class ForgeRegistry<V extends IForgeRegistryEntry<V>> implements
		IForgeRegistryModifiable<V>, IForgeRegistryInternal<V>, RegistryEntryAddedCallback<V> {
	public static Marker REGISTRIES = MarkerManager.getMarker("REGISTRIES");
	private static Logger LOGGER = LogManager.getLogger();
	private final Identifier name;
	private final boolean isVanilla;
	private final Registry<V> vanilla;
	private final Class<V> superType;
	private final Map<Identifier, ?> slaves = new HashMap<>();
	private final CreateCallback<V> create;
	private final AddCallback<V> add;
	private final ClearCallback<V> clear;
	private final RegistryManager stage;
	private final boolean allowOverrides;
	private final boolean isModifiable;

	private boolean isFrozen = false;
	private V oldValue; // context of AddCallback, does not use elsewhere

	/**
	 * Called by RegistryBuilder, for modded registries.
	 * @param stage
	 * @param name
	 * @param builder
	 */
	@SuppressWarnings("unchecked")
	public ForgeRegistry(RegistryManager stage, Identifier name, RegistryBuilder<V> builder) {
		this.stage = stage;
		this.name = name;
		this.superType = builder.getType();
		this.create = builder.getCreate();
		this.add = builder.getAdd();
		this.clear = builder.getClear();
		this.allowOverrides = builder.getAllowOverrides();
		this.isModifiable = builder.getAllowModifications();

		Registry<V> vanilla = builder.getVanillaRegistry();

		if (vanilla == null) {
			// Forge modded registry
			this.vanilla = new ExtendedVanillaRegistry<>(this);
			Registry.REGISTRIES.add(name, (MutableRegistry) this.vanilla);
			this.isVanilla = false;
		} else {
			// Vanilla registry
			this.vanilla = vanilla;
			((VanillaRegistry) this.vanilla).setForgeRegistry(this);
			this.isVanilla = true;
		}

		// Fabric hooks
		// TODO: Some vanilla registry types are not patched yet, add this check to avoid crash
		if (IForgeRegistryEntry.class.isAssignableFrom(this.superType)) {
			RegistryEntryAddedCallback.event(this.vanilla).register(this);
		}

		if (this.create != null) {
			this.create.onCreate(this, stage);
		}
	}

	@Override
	public void onEntryAdded(int rawId, Identifier id, V newValue) {
		if (this.add != null) {
			this.add.onAdd(this, this.stage, rawId, newValue, this.oldValue);
		}
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

		if (potentialOldValue.isPresent()) {
			V oldValue = potentialOldValue.get();

			if (oldValue == value) {
				LOGGER.warn(REGISTRIES, "Registry {}: The object {} has been registered twice for the same name {}.", this.superType.getSimpleName(), value, identifier);
				return;
			} else if (this.allowOverrides) {
				this.oldValue = oldValue;
				LOGGER.debug(REGISTRIES, "Registry {}: The object {} {} has been overridden by {}.", this.superType.getSimpleName(), identifier, oldValue, value);
			} else {
				throw new IllegalArgumentException(String.format("The name %s has been registered twice, for %s and %s.", identifier, oldValue, value));
			}
		} else {
			this.oldValue = null;
		}

		Identifier oldIdentifier = vanilla.getId(value);

		if (oldIdentifier != getDefaultKey()) {
			throw new IllegalArgumentException(String.format("The object %s{%x} has been registered twice, using the names %s and %s.", value, System.identityHashCode(value), oldIdentifier, identifier));
		}

		Registry.register(vanilla, identifier, value);
		this.oldValue = null; // Clear the onAddEntry context
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

	@Override
	public String toString() {
		String type = this.isVanilla ? "Vanilla" : "Mod";
		return type + ", " + this.name.toString();
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

	/**
	 * Used to control the times where people can modify this registry.
	 * Users should only ever register things in the Register<?> events!
	 */
	public void freeze() {
		this.isFrozen = true;
	}

	public void unfreeze() {
		this.isFrozen = false;
	}

	@Override
	public boolean isLocked() {
		return this.isFrozen;
	}

	@Override
	public void clear() {
		if (!this.isModifiable) {
			throw new UnsupportedOperationException("Attempted to clear a non-modifiable Forge Registry");
		}

		if (this.isLocked()) {
			throw new IllegalStateException("Attempted to clear the registry too late.");
		}

		if (!this.isVanilla) {
			throw new UnsupportedOperationException("Vanilla registry does not support clear().");
		}

		if (this.clear != null) {
			this.clear.onClear(this, stage);
		}

		ExtendedVanillaRegistry<V> reg = (ExtendedVanillaRegistry<V>) this.vanilla;
		reg.clear();
	}

	// TODO: implement remove()
	@Override
	public V remove(Identifier key) {
		throw new UnsupportedOperationException("Remove() is not implemented");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getSlaveMap(Identifier name, Class<T> type) {
		return (T) this.slaves.get(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setSlaveMap(Identifier name, Object obj) {
		((Map<Identifier, Object>) this.slaves).put(name, obj);
	}
}
