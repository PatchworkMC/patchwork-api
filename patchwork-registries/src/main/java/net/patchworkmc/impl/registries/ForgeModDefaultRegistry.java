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

import java.util.HashSet;
import java.util.Set;

import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import com.google.common.collect.BiMap;

import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.registry.DefaultedRegistry;

/**
 * The vanilla field {@link net.minecraft.util.registry.SimpleRegistry#indexedEntries} is not used.
 * @author Rikka0w0
 */
public class ForgeModDefaultRegistry<V extends IForgeRegistryEntry<V>> extends DefaultedRegistry<V> implements ForgeModRegistryImpl<V> {
	protected final Set<Integer> availabilityMap = new HashSet<>();

	private final ForgeRegistry<V> forgeRegistry;
	public ForgeModDefaultRegistry(ForgeRegistry<V> forgeRegistry, RegistryBuilder<V> builder) {
		super(builder.getDefault().toString());
		this.forgeRegistry = forgeRegistry;
	}

	@Override
	public ForgeRegistry patchwork$getForgeRegistry() {
		return this.forgeRegistry;
	}

	/////////////////////////////
	/// DefaultedRegistry
	/////////////////////////////
	@Override
	public <T extends V> T set(int rawId, Identifier id, T entry) {
		return this.setImpl(rawId, id, entry);
	}

	@Override
	public <T extends V> T add(Identifier id, T entry) {
		return this.addImpl(id, entry);
	}

	/////////////////////////////
	/// ForgeModRegistryImpl
	/////////////////////////////
	@Override
	public BiMap<Identifier, V> entries() {
		return this.idToEntry;
	}

	@Override
	public Int2ObjectBiMap<V> indexedEntries() {
		return this.indexedEntries;
	}

	@Override
	public Set<Integer> availabilityMap() {
		return this.availabilityMap;
	}

	@Override
	public void randomEntriesClear() {
		this.randomEntries = null;
	}
}
