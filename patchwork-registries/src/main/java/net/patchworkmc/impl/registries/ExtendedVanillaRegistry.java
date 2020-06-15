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
import java.util.Iterator;

import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.commons.lang3.Validate;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.SimpleRegistry;

/**
 * The vanilla field {@link net.minecraft.util.registry.SimpleRegistry#indexedEntries} is not used.
 * @author Rikka0w0
 */
public class ExtendedVanillaRegistry<V extends IForgeRegistryEntry<V>> extends SimpleRegistry<V> implements ForgeRegistryProvider, ModifiableRegistry<V> {
	protected final BiMap<Integer, V> ids = HashBiMap.create();
	protected final BitSet availabilityMap = new BitSet(256);
	protected final int min, max;

	private final ForgeRegistry<V> forgeRegistry;
	public ExtendedVanillaRegistry(ForgeRegistry<V> forgeRegistry, RegistryBuilder<V> builder) {
		this.forgeRegistry = forgeRegistry;
		this.min = builder.getMinId();
		this.max = builder.getMaxId();
	}

	@Override
	public void clear() {
		this.entries.clear();
		this.indexedEntries.clear();
		this.randomEntries = null;
	}

	@Override
	public ForgeRegistry getForgeRegistry() {
		return this.forgeRegistry;
	}

	protected int getNextId(int rawId) {
		int idToUse = rawId;

		if (idToUse < 0 || this.availabilityMap.get(idToUse)) {
			idToUse = this.availabilityMap.nextClearBit(0);
		}

		if (idToUse > this.max) {
			throw new RuntimeException(String.format("Invalid id %d - maximum id range exceeded.", idToUse));
		}

		return idToUse;
	}

	@Override
	public <T extends V> T set(int rawId, Identifier id, T entry) {
		int idToUse;
		Validate.notNull(id);
		Validate.notNull(entry);
		V oldEntry = get(id);

		if (oldEntry != null) {
			// Replace old
			idToUse = this.getRawId(oldEntry);
		} else {
			// Insert new
			idToUse = this.getNextId(rawId);
		}

		this.randomEntries = null;
		this.entries.put(id, entry);
		this.ids.put(idToUse, entry);
		this.availabilityMap.set(idToUse);

		return entry;
	}

	@Override
	public <T extends V> T add(Identifier id, T entry) {
		return this.set(-1, id, entry);
	}

	@Override
	public int getRawId(V entry) {
		return this.ids.inverse().get(entry);
	}

	@Override
	public V get(int index) {
		return this.ids.get(index);
	}

	@Override
	public Iterator<V> iterator() {
		return this.ids.values().iterator();
	}

	@Override
	public V remove(Identifier key) {
		V value = this.entries.remove(key);

		if (value != null) {
			int oldId = this.ids.inverse().remove(value);

			if (key == null) {
				throw new IllegalStateException("Removed a entry that did not have an associated id: " + key + " " + value.toString() + " This should never happen unless hackery!");
			}
		}

		return value;
	}
}
