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

package net.minecraftforge.common;

import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.fabricmc.fabric.api.biomes.v1.FabricBiomes;
import net.fabricmc.fabric.api.biomes.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biomes.v1.OverworldClimate;

public class BiomeManager {
	public static void addSpawnBiome(Biome biome) {
		FabricBiomes.addSpawnBiome(biome);
	}

	public static void addBiome(BiomeType type, BiomeEntry entry) {
		Objects.requireNonNull(type, "type must not be null");
		Objects.requireNonNull(entry, "entry must not be null");

		OverworldClimate climate = type.getClimate();

		OverworldBiomes.addContinentalBiome(entry.biome, climate, entry.getWeight() / 10.0);
	}

	public enum BiomeType {
		DESERT, WARM, COOL, ICY;

		private OverworldClimate getClimate() {
			switch (this) {
			case DESERT:
				return OverworldClimate.DRY;
			case WARM:
				return OverworldClimate.TEMPERATE;
			case COOL:
				return OverworldClimate.COOL;
			case ICY:
				return OverworldClimate.SNOWY;
			default:
				throw new IllegalStateException("Someone's been tampering with the BiomeType enum!");
			}
		}
	}

	public static class BiomeEntry extends WeightedPicker.Entry {
		public final Biome biome;

		public int field_76292_a; // FIXME: workaround for https://github.com/PatchworkMC/patchwork-patcher/issues/57, will likely break things

		public BiomeEntry(Biome biome, int weight) {
			super(weight);

			this.field_76292_a = weight;
			this.biome = biome;
		}

		private int getWeight() {
			return this.weight;
		}
	}

	// Biomes O' Plenty pokes Forge internals. Fun
	private static TrackedList<BiomeEntry>[] biomes = setupBiomes();

	private static TrackedList<BiomeEntry>[] setupBiomes() {
		@SuppressWarnings("unchecked")
		TrackedList<BiomeEntry>[] currentBiomes = new TrackedList[BiomeType.values().length];
		List<BiomeEntry> list = new ArrayList<BiomeEntry>();

		list.add(new BiomeEntry(BiomeKeys.FOREST, 10));
		list.add(new BiomeEntry(BiomeKeys.DARK_FOREST, 10));
		list.add(new BiomeEntry(BiomeKeys.MOUNTAINS, 10));
		list.add(new BiomeEntry(BiomeKeys.PLAINS, 10));
		list.add(new BiomeEntry(BiomeKeys.BIRCH_FOREST, 10));
		list.add(new BiomeEntry(BiomeKeys.SWAMP, 10));

		currentBiomes[BiomeType.WARM.ordinal()] = new TrackedList<BiomeEntry>(list);
		list.clear();

		list.add(new BiomeEntry(BiomeKeys.FOREST, 10));
		list.add(new BiomeEntry(BiomeKeys.MOUNTAINS, 10));
		list.add(new BiomeEntry(BiomeKeys.TAIGA, 10));
		list.add(new BiomeEntry(BiomeKeys.PLAINS, 10));

		currentBiomes[BiomeType.COOL.ordinal()] = new TrackedList<BiomeEntry>(list);
		list.clear();

		list.add(new BiomeEntry(BiomeKeys.SNOWY_TUNDRA, 30));
		list.add(new BiomeEntry(BiomeKeys.SNOWY_TAIGA, 10));

		currentBiomes[BiomeType.ICY.ordinal()] = new TrackedList<BiomeEntry>(list);
		list.clear();

		currentBiomes[BiomeType.DESERT.ordinal()] = new TrackedList<BiomeEntry>(list);

		return currentBiomes;
	}

	public static ImmutableList<BiomeEntry> getBiomes(BiomeType type) {
		int idx = type.ordinal();
		// We want to return null instead of throw an exception if the index is out of bounds.
		List<BiomeEntry> list = idx >= biomes.length ? null : biomes[idx];

		return list != null ? ImmutableList.copyOf(list) : null;
	}

	private static class TrackedList<E> extends ArrayList<E> {
		private static final long serialVersionUID = 1L;
		private boolean isModded = false;

		TrackedList(Collection<? extends E> c) {
			super(c);
		}

		@Override
		public E set(int index, E element) {
			isModded = true;
			return super.set(index, element);
		}

		@Override
		public boolean add(E e) {
			isModded = true;
			return super.add(e);
		}

		@Override
		public void add(int index, E element) {
			isModded = true;
			super.add(index, element);
		}

		@Override
		public E remove(int index) {
			isModded = true;
			return super.remove(index);
		}

		@Override
		public boolean remove(Object o) {
			isModded = true;
			return super.remove(o);
		}

		@Override
		public void clear() {
			isModded = true;
			super.clear();
		}

		@Override
		public boolean addAll(Collection<? extends E> c) {
			isModded = true;
			return super.addAll(c);
		}

		@Override
		public boolean addAll(int index, Collection<? extends E> c) {
			isModded = true;
			return super.addAll(index, c);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			isModded = true;
			return super.removeAll(c);
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			isModded = true;
			return super.retainAll(c);
		}

		public boolean isModded() {
			return isModded;
		}
	}
}
