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
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.SimpleRegistry;

/**
 * The vanilla field {@link net.minecraft.util.registry.SimpleRegistry#indexedEntries} is not used.
 * @author Rikka0w0
 */
public class ForgeModRegistry<V extends IForgeRegistryEntry<V>> extends SimpleRegistry<V> implements ForgeModRegistryImpl<V> {
	protected final BiMap<Integer, V> ids = HashBiMap.create();
	protected final BitSet availabilityMap = new BitSet(256);

	private final ForgeRegistry<V> forgeRegistry;
	public ForgeModRegistry(ForgeRegistry<V> forgeRegistry, RegistryBuilder<V> builder) {
		this.forgeRegistry = forgeRegistry;
	}

	@Override
	public ForgeRegistry getForgeRegistry() {
		return this.forgeRegistry;
	}

	/////////////////////////////
	/// SimpleRegistry
	/////////////////////////////
	@Override
	public <T extends V> T set(int rawId, Identifier id, T entry) {
		return this.setImpl(rawId, id, entry);
	}

	@Override
	public <T extends V> T add(Identifier id, T entry) {
		return this.addImpl(id, entry);
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

	/////////////////////////////
	/// ForgeModRegistryImpl
	/////////////////////////////
	@Override
	public BiMap<Identifier, V> entries() {
		return this.entries;
	}

	@Override
	public BiMap<Integer, V> ids() {
		return this.ids;
	}

	@Override
	public BitSet availabilityMap() {
		return this.availabilityMap;
	}

	@Override
	public void randomEntriesClear() {
		this.randomEntries = null;
	}
}
