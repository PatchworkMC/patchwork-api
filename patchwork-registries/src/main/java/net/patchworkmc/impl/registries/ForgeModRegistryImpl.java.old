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

package net.patchworkmc.impl.registries;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import com.google.common.collect.BiMap;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;

/**
 * This interface is for avoiding duplicated implementation as much as possible
 * The vanilla field {@link net.minecraft.util.registry.SimpleRegistry#indexedEntries} is not used.
 * @author Rikka0w0
 */
public interface ForgeModRegistryImpl<V extends IForgeRegistryEntry<V>> extends ForgeRegistryProvider, RemovableRegistry<V> {
	//////////////////////////
	/// Getters
	//////////////////////////
	Int2ObjectBiMap<V> indexedEntries();
	BiMap<Identifier, V> entries();
	void randomEntriesClear();
	Set<Integer> availabilityMap();

	//////////////////////////
	/// Implementations
	//////////////////////////
	default int getNextId(int rawId) {
		if (rawId > patchwork$getForgeRegistry().max) {
			throw new RuntimeException(String.format("Invalid id %d - maximum id range exceeded.", rawId));
		}

		if (rawId < 0 || indexedEntries().get(rawId) != null) {
			// rawId is invalid, not specified or occupied

			Set<Integer> availableIds = availabilityMap();

			if (availableIds.isEmpty()) {
				// Attempt to place the new entry at the end
				rawId = entries().size();

				if (indexedEntries().get(rawId) != null) {
					for (rawId = patchwork$getForgeRegistry().min; rawId <= patchwork$getForgeRegistry().max; rawId++) {
						if (indexedEntries().get(rawId) == null) {
							break;
						}
					}
				}
			} else {
				// Reuse rawId
				Iterator<Integer> iterator = availableIds.iterator();
				rawId = iterator.next();
				iterator.remove();
			}
		}

		return rawId;
	}

	@SuppressWarnings("unchecked")
	default <T extends V> T setImpl(int rawId, Identifier id, T entry) {
		int idToUse;
		Validate.notNull(id);
		Validate.notNull(entry);
		V oldEntry = ((Registry<V>) this).get(id);

		if (oldEntry != null) {
			// Replace old
			idToUse = ((Registry<V>) this).getRawId(oldEntry);
		} else {
			// Insert new
			idToUse = this.getNextId(rawId);
		}

		randomEntriesClear();
		entries().put(id, entry);
		indexedEntries().put(entry, idToUse);

		// To maintain a consistent behavior with vanilla registery and fabric
		// Needs to fire the onEntryAdded() event here, since SimpleRegistry#set is not called
		RegistryEntryAddedCallback.event((Registry<V>) this).invoker().onEntryAdded(rawId, id, entry);
		return entry;
	}

	default <T extends V> T addImpl(Identifier id, T entry) {
		return setImpl(-1, id, entry);
	}

	//////////////////////////
	/// RemovableRegistry<V>
	//////////////////////////
	@Override
	default V remove(Identifier key) {
		V value = entries().remove(key);

		if (value != null) {
			int oldId = ((RemovableInt2ObjectBiMap<V>) (Object) indexedEntries()).patchwork$remove(value);

			if (key == null) {
				throw new IllegalStateException("Removed a entry that did not have an associated id: " + key + " " + value.toString() + " This should never happen unless hackery!");
			}

			if (oldId < entries().size()) {
				availabilityMap().add(oldId);
			}

			randomEntriesClear();
		}

		return value;
	}

	@Override
	default void clear() {
		indexedEntries().clear();
		entries().clear();
		availabilityMap().clear();
		randomEntriesClear();
	}
}
