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

import java.util.BitSet;

import org.apache.commons.lang3.Validate;
import com.google.common.collect.BiMap;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;

/**
 * This interface is for avoiding duplicated implementation as much as possible
 * The vanilla field {@link net.minecraft.util.registry.SimpleRegistry#indexedEntries} is not used.
 * @author Rikka0w0
 */
public interface ForgeModRegistryImpl<V extends IForgeRegistryEntry<V>> extends ForgeRegistryProvider, EditableRegistry<V> {
	//////////////////////////
	/// Getters
	//////////////////////////
	BiMap<Identifier, V> entries();
	BiMap<Integer, V> ids();
	BitSet availabilityMap();
	void randomEntriesClear();

	//////////////////////////
	/// Implementations
	//////////////////////////
	default int getNextId(int rawId) {
		int idToUse = rawId;

		if (idToUse < 0 || availabilityMap().get(idToUse)) {
			idToUse = availabilityMap().nextClearBit(getForgeRegistry().min);
		}

		if (idToUse > getForgeRegistry().max) {
			throw new RuntimeException(String.format("Invalid id %d - maximum id range exceeded.", idToUse));
		}

		return idToUse;
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
		ids().put(idToUse, entry);
		availabilityMap().set(idToUse);

		// To maintain a consistent behavior with vanilla registery and fabric
		// Needs to fire the onEntryAdded() event here, since SimpleRegistry#set is not called
		RegistryEntryAddedCallback.event((Registry<V>) this).invoker().onEntryAdded(rawId, id, entry);
		return entry;
	}

	default <T extends V> T addImpl(Identifier id, T entry) {
		return setImpl(-1, id, entry);
	}

	//////////////////////////
	/// ModifiableRegistry<V>
	//////////////////////////
	@Override
	default V remove(Identifier key) {
		V value = entries().remove(key);

		if (value != null) {
			int oldId = ids().inverse().remove(value);

			if (key == null) {
				throw new IllegalStateException("Removed a entry that did not have an associated id: " + key + " " + value.toString() + " This should never happen unless hackery!");
			}

			availabilityMap().clear(oldId);
			randomEntriesClear();
		}

		return value;
	}

	@Override
	default void clear() {
		entries().clear();
		ids().clear();
		availabilityMap().clear(0, availabilityMap().length());
		randomEntriesClear();
	}
}
