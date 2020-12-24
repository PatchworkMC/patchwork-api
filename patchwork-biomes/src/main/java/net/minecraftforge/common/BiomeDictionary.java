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

import static net.minecraftforge.common.BiomeDictionary.Type.BEACH;
import static net.minecraftforge.common.BiomeDictionary.Type.COLD;
import static net.minecraftforge.common.BiomeDictionary.Type.CONIFEROUS;
import static net.minecraftforge.common.BiomeDictionary.Type.DENSE;
import static net.minecraftforge.common.BiomeDictionary.Type.DRY;
import static net.minecraftforge.common.BiomeDictionary.Type.END;
import static net.minecraftforge.common.BiomeDictionary.Type.FOREST;
import static net.minecraftforge.common.BiomeDictionary.Type.HILLS;
import static net.minecraftforge.common.BiomeDictionary.Type.HOT;
import static net.minecraftforge.common.BiomeDictionary.Type.JUNGLE;
import static net.minecraftforge.common.BiomeDictionary.Type.MESA;
import static net.minecraftforge.common.BiomeDictionary.Type.MOUNTAIN;
import static net.minecraftforge.common.BiomeDictionary.Type.MUSHROOM;
import static net.minecraftforge.common.BiomeDictionary.Type.NETHER;
import static net.minecraftforge.common.BiomeDictionary.Type.OCEAN;
import static net.minecraftforge.common.BiomeDictionary.Type.PLAINS;
import static net.minecraftforge.common.BiomeDictionary.Type.RARE;
import static net.minecraftforge.common.BiomeDictionary.Type.RIVER;
import static net.minecraftforge.common.BiomeDictionary.Type.SANDY;
import static net.minecraftforge.common.BiomeDictionary.Type.SAVANNA;
import static net.minecraftforge.common.BiomeDictionary.Type.SNOWY;
import static net.minecraftforge.common.BiomeDictionary.Type.SPARSE;
import static net.minecraftforge.common.BiomeDictionary.Type.SPOOKY;
import static net.minecraftforge.common.BiomeDictionary.Type.SWAMP;
import static net.minecraftforge.common.BiomeDictionary.Type.VOID;
import static net.minecraftforge.common.BiomeDictionary.Type.WASTELAND;
import static net.minecraftforge.common.BiomeDictionary.Type.WET;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public class BiomeDictionary {
	private static final boolean DEBUG = false;
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<Identifier, BiomeInfo> biomeInfoMap = new HashMap<>();

	static {
		registerVanillaBiomes();
	}

	/**
	 * Adds the given {@link Type}s to the {@link Biome}.
	 */
	public static void addTypes(Biome biome, Type... types) {
		Preconditions.checkArgument(Registry.BIOME.getId(biome) != null, "Cannot add types to unregistered biome %s", biome);

		Collection<Type> supertypes = listSupertypes(types);
		Collections.addAll(supertypes, types);

		for (Type type : supertypes) {
			type.biomes.add(biome);
		}

		BiomeInfo biomeInfo = getBiomeInfo(biome);
		Collections.addAll(biomeInfo.types, types);
		biomeInfo.types.addAll(supertypes);
	}

	/**
	 * Gets the set of {@link Biome} instances that have the given type.
	 */
	public static Set<Biome> getBiomes(Type type) {
		return type.biomesUnmodifiable;
	}

	/**
	 * Gets the set of types that have been added to the given {@link Biome}.
	 */
	public static Set<Type> getTypes(Biome biome) {
		ensureHasTypes(biome);

		return getBiomeInfo(biome).typesUnmodifiable;
	}

	/**
	 * Checks if the two given {@link Biome} instances have types in common.
	 *
	 * @return <code>true</code> if a common type is found, <code>false</code> otherwise
	 */
	public static boolean areSimilar(Biome biomeA, Biome biomeB) {
		for (Type type : getTypes(biomeA)) {
			if (getTypes(biomeB).contains(type)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks if the given type has been added to the given {@link Biome}.
	 */
	public static boolean hasType(Biome biome, Type type) {
		return getTypes(biome).contains(type);
	}

	/**
	 * Checks if any type has been added to the given {@link Biome}.
	 */
	public static boolean hasAnyType(Biome biome) {
		return !getBiomeInfo(biome).types.isEmpty();
	}

	/**
	 * Automatically adds appropriate types to a given {@link Biome} based on certain heuristics.
	 *
	 * <p>If a {@link Biome}'s types are requested and no types have been added to the {@link Biome} so far, its types
	 * will be determined and added using this method.</p>
	 */
	public static void makeBestGuess(Biome biome) {
		Type type = Type.fromVanilla(biome.getCategory());

		if (type != null) {
			BiomeDictionary.addTypes(biome, type);
		}

		if (biome.getDownfall() > 0.85f) {
			BiomeDictionary.addTypes(biome, WET);
		}

		if (biome.getDownfall() < 0.15f) {
			BiomeDictionary.addTypes(biome, DRY);
		}

		if (biome.getTemperature() > 0.85f) {
			BiomeDictionary.addTypes(biome, HOT);
		}

		if (biome.getTemperature() < 0.15f) {
			BiomeDictionary.addTypes(biome, COLD);
		}

		if (biome.hasHighHumidity() && biome.getDepth() < 0.0F && (biome.getScale() <= 0.3F && biome.getScale() >= 0.0F)) {
			BiomeDictionary.addTypes(biome, SWAMP);
		}

		if (biome.getDepth() <= -0.5F) {
			if (biome.getScale() == 0.0F) {
				BiomeDictionary.addTypes(biome, RIVER);
			} else {
				BiomeDictionary.addTypes(biome, OCEAN);
			}
		}

		if (biome.getScale() >= 0.4F && biome.getScale() < 1.5F) {
			BiomeDictionary.addTypes(biome, HILLS);
		}

		if (biome.getScale() >= 1.5F) {
			BiomeDictionary.addTypes(biome, MOUNTAIN);
		}
	}

	//Internal implementation
	private static BiomeInfo getBiomeInfo(Biome biome) {
		return biomeInfoMap.computeIfAbsent(Registry.BIOME.getId(biome), k -> new BiomeInfo());
	}

	/**
	 * Ensure that at least one type has been added to the given {@link Biome}.
	 */
	private static void ensureHasTypes(Biome biome) {
		if (!hasAnyType(biome)) {
			makeBestGuess(biome);
			LOGGER.warn("No types have been added to Biome {}, types have been assigned on a best-effort guess: {}", Registry.BIOME.getId(biome), getTypes(biome));
		}
	}

	private static Collection<Type> listSupertypes(Type... types) {
		Set<Type> supertypes = new HashSet<>();
		Deque<Type> next = new ArrayDeque<>();
		Collections.addAll(next, types);

		while (!next.isEmpty()) {
			Type type = next.remove();

			for (Type sType : Type.BY_NAME.values()) {
				if (sType.subTypes.contains(type) && supertypes.add(sType)) {
					next.add(sType);
				}
			}
		}

		return supertypes;
	}

	private static void registerVanillaBiomes() {
		addTypes(BiomeKeys.OCEAN, OCEAN);
		addTypes(BiomeKeys.PLAINS, PLAINS);
		addTypes(BiomeKeys.DESERT, HOT, DRY, SANDY);
		addTypes(BiomeKeys.MOUNTAINS, MOUNTAIN, HILLS);
		addTypes(BiomeKeys.FOREST, FOREST);
		addTypes(BiomeKeys.TAIGA, COLD, CONIFEROUS, FOREST);
		addTypes(BiomeKeys.SWAMP, WET, SWAMP);
		addTypes(BiomeKeys.RIVER, RIVER);
		addTypes(BiomeKeys.NETHER_WASTES, HOT, DRY, NETHER);
		addTypes(BiomeKeys.THE_END, COLD, DRY, END);
		addTypes(BiomeKeys.FROZEN_OCEAN, COLD, OCEAN, SNOWY);
		addTypes(BiomeKeys.FROZEN_RIVER, COLD, RIVER, SNOWY);
		addTypes(BiomeKeys.SNOWY_TUNDRA, COLD, SNOWY, WASTELAND);
		addTypes(BiomeKeys.SNOWY_MOUNTAINS, COLD, SNOWY, MOUNTAIN);
		addTypes(BiomeKeys.MUSHROOM_FIELDS, MUSHROOM, RARE);
		addTypes(BiomeKeys.MUSHROOM_FIELD_SHORE, MUSHROOM, BEACH, RARE);
		addTypes(BiomeKeys.BEACH, BEACH);
		addTypes(BiomeKeys.DESERT_HILLS, HOT, DRY, SANDY, HILLS);
		addTypes(BiomeKeys.WOODED_HILLS, FOREST, HILLS);
		addTypes(BiomeKeys.TAIGA_HILLS, COLD, CONIFEROUS, FOREST, HILLS);
		addTypes(BiomeKeys.MOUNTAIN_EDGE, MOUNTAIN);
		addTypes(BiomeKeys.JUNGLE, HOT, WET, DENSE, JUNGLE);
		addTypes(BiomeKeys.JUNGLE_HILLS, HOT, WET, DENSE, JUNGLE, HILLS);
		addTypes(BiomeKeys.JUNGLE_EDGE, HOT, WET, JUNGLE, FOREST, RARE);
		addTypes(BiomeKeys.DEEP_OCEAN, OCEAN);
		addTypes(BiomeKeys.STONE_SHORE, BEACH);
		addTypes(BiomeKeys.SNOWY_BEACH, COLD, BEACH, SNOWY);
		addTypes(BiomeKeys.BIRCH_FOREST, FOREST);
		addTypes(BiomeKeys.BIRCH_FOREST_HILLS, FOREST, HILLS);
		addTypes(BiomeKeys.DARK_FOREST, SPOOKY, DENSE, FOREST);
		addTypes(BiomeKeys.SNOWY_TAIGA, COLD, CONIFEROUS, FOREST, SNOWY);
		addTypes(BiomeKeys.SNOWY_TAIGA_HILLS, COLD, CONIFEROUS, FOREST, SNOWY, HILLS);
		addTypes(BiomeKeys.GIANT_TREE_TAIGA, COLD, CONIFEROUS, FOREST);
		addTypes(BiomeKeys.GIANT_TREE_TAIGA_HILLS, COLD, CONIFEROUS, FOREST, HILLS);
		addTypes(BiomeKeys.WOODED_MOUNTAINS, MOUNTAIN, FOREST, SPARSE);
		addTypes(BiomeKeys.SAVANNA, HOT, SAVANNA, PLAINS, SPARSE);
		addTypes(BiomeKeys.SAVANNA_PLATEAU, HOT, SAVANNA, PLAINS, SPARSE, RARE);
		addTypes(BiomeKeys.BADLANDS, MESA, SANDY, DRY);
		addTypes(BiomeKeys.WOODED_BADLANDS_PLATEAU, MESA, SANDY, DRY, SPARSE);
		addTypes(BiomeKeys.BADLANDS_PLATEAU, MESA, SANDY, DRY);
		addTypes(BiomeKeys.SMALL_END_ISLANDS, END);
		addTypes(BiomeKeys.END_MIDLANDS, END);
		addTypes(BiomeKeys.END_HIGHLANDS, END);
		addTypes(BiomeKeys.END_BARRENS, END);
		addTypes(BiomeKeys.WARM_OCEAN, OCEAN, HOT);
		addTypes(BiomeKeys.LUKEWARM_OCEAN, OCEAN);
		addTypes(BiomeKeys.COLD_OCEAN, OCEAN, COLD);
		addTypes(BiomeKeys.DEEP_WARM_OCEAN, OCEAN, HOT);
		addTypes(BiomeKeys.DEEP_LUKEWARM_OCEAN, OCEAN);
		addTypes(BiomeKeys.DEEP_COLD_OCEAN, OCEAN, COLD);
		addTypes(BiomeKeys.DEEP_FROZEN_OCEAN, OCEAN, COLD);
		addTypes(BiomeKeys.THE_VOID, VOID);
		addTypes(BiomeKeys.SUNFLOWER_PLAINS, PLAINS, RARE);
		addTypes(BiomeKeys.DESERT_LAKES, HOT, DRY, SANDY, RARE);
		addTypes(BiomeKeys.GRAVELLY_MOUNTAINS, MOUNTAIN, SPARSE, RARE);
		addTypes(BiomeKeys.FLOWER_FOREST, FOREST, HILLS, RARE);
		addTypes(BiomeKeys.TAIGA_MOUNTAINS, COLD, CONIFEROUS, FOREST, MOUNTAIN, RARE);
		addTypes(BiomeKeys.SWAMP_HILLS, WET, SWAMP, HILLS, RARE);
		addTypes(BiomeKeys.ICE_SPIKES, COLD, SNOWY, HILLS, RARE);
		addTypes(BiomeKeys.MODIFIED_JUNGLE, HOT, WET, DENSE, JUNGLE, MOUNTAIN, RARE);
		addTypes(BiomeKeys.MODIFIED_JUNGLE_EDGE, HOT, SPARSE, JUNGLE, HILLS, RARE);
		addTypes(BiomeKeys.TALL_BIRCH_FOREST, FOREST, DENSE, HILLS, RARE);
		addTypes(BiomeKeys.TALL_BIRCH_HILLS, FOREST, DENSE, MOUNTAIN, RARE);
		addTypes(BiomeKeys.DARK_FOREST_HILLS, SPOOKY, DENSE, FOREST, MOUNTAIN, RARE);
		addTypes(BiomeKeys.SNOWY_TAIGA_MOUNTAINS, COLD, CONIFEROUS, FOREST, SNOWY, MOUNTAIN, RARE);
		addTypes(BiomeKeys.GIANT_SPRUCE_TAIGA, DENSE, FOREST, RARE);
		addTypes(BiomeKeys.GIANT_SPRUCE_TAIGA_HILLS, DENSE, FOREST, HILLS, RARE);
		addTypes(BiomeKeys.MODIFIED_GRAVELLY_MOUNTAINS, MOUNTAIN, SPARSE, RARE);
		addTypes(BiomeKeys.SHATTERED_SAVANNA, HOT, DRY, SPARSE, SAVANNA, MOUNTAIN, RARE);
		addTypes(BiomeKeys.SHATTERED_SAVANNA_PLATEAU, HOT, DRY, SPARSE, SAVANNA, HILLS, RARE);
		addTypes(BiomeKeys.ERODED_BADLANDS, HOT, DRY, SPARSE, MOUNTAIN, RARE);
		addTypes(BiomeKeys.MODIFIED_WOODED_BADLANDS_PLATEAU, HOT, DRY, SPARSE, HILLS, RARE);
		addTypes(BiomeKeys.MODIFIED_BADLANDS_PLATEAU, HOT, DRY, SPARSE, MOUNTAIN, RARE);

		if (DEBUG) {
			StringBuilder buf = new StringBuilder();
			buf.append("BiomeDictionary:\n");
			Type.BY_NAME.forEach((name, type) -> buf.append("    ").append(type.name).append(": ").append(type.biomes.stream().map(b -> Registry.BIOME.getId(b).toString()).collect(Collectors.joining(", "))).append('\n'));
			LOGGER.debug(buf.toString());
		}
	}

	public static final class Type {
		// NB: These fields *must* be at the top of the class, otherwise an ExceptionInInitializerError will result.
		private static final Map<String, Type> BY_NAME = new HashMap<>();
		private static final Collection<Type> ALL_TYPES = Collections.unmodifiableCollection(BY_NAME.values());

		/*Temperature-based tags. Specifying neither implies a biome is temperate*/
		public static final Type HOT = new Type("HOT");
		public static final Type COLD = new Type("COLD");
		/*Tags specifying the amount of vegetation a biome has. Specifying neither implies a biome to have moderate amounts*/
		public static final Type SPARSE = new Type("SPARSE");
		public static final Type DENSE = new Type("DENSE");
		/*Tags specifying how moist a biome is. Specifying neither implies the biome as having moderate humidity*/
		public static final Type WET = new Type("WET");
		public static final Type DRY = new Type("DRY");
		/*Tree-based tags, SAVANNA refers to dry, desert-like trees (Such as Acacia), CONIFEROUS refers to snowy trees (Such as Spruce) and JUNGLE refers to jungle trees.
		 * Specifying no tag implies a biome has temperate trees (Such as Oak)*/
		public static final Type SAVANNA = new Type("SAVANNA");
		public static final Type CONIFEROUS = new Type("CONIFEROUS");
		public static final Type JUNGLE = new Type("JUNGLE");
		/*Tags specifying the nature of a biome*/
		public static final Type SPOOKY = new Type("SPOOKY");
		public static final Type DEAD = new Type("DEAD");
		public static final Type LUSH = new Type("LUSH");
		public static final Type NETHER = new Type("NETHER");
		public static final Type END = new Type("END");
		public static final Type MUSHROOM = new Type("MUSHROOM");
		public static final Type MAGICAL = new Type("MAGICAL");
		public static final Type RARE = new Type("RARE");
		public static final Type OCEAN = new Type("OCEAN");
		public static final Type RIVER = new Type("RIVER");
		/**
		 * A general tag for all water-based biomes. Shown as present if OCEAN or RIVER are.
		 */
		public static final Type WATER = new Type("WATER", OCEAN, RIVER);
		/*Generic types which a biome can be*/
		public static final Type MESA = new Type("MESA");
		public static final Type FOREST = new Type("FOREST");
		public static final Type PLAINS = new Type("PLAINS");
		public static final Type MOUNTAIN = new Type("MOUNTAIN");
		public static final Type HILLS = new Type("HILLS");
		public static final Type SWAMP = new Type("SWAMP");
		public static final Type SANDY = new Type("SANDY");
		public static final Type SNOWY = new Type("SNOWY");
		public static final Type WASTELAND = new Type("WASTELAND");
		public static final Type BEACH = new Type("BEACH");
		public static final Type VOID = new Type("VOID");
		private final String name;
		private final List<Type> subTypes;
		private final Set<Biome> biomes = new HashSet<>();
		private final Set<Biome> biomesUnmodifiable = Collections.unmodifiableSet(biomes);

		private Type(String name, Type... subTypes) {
			this.name = name;
			this.subTypes = ImmutableList.copyOf(subTypes);

			BY_NAME.put(name, this);
		}

		/**
		 * Retrieves a Type instance by name,
		 * if one does not exist already it creates one.
		 *
		 * <p>This can be used as intermediate measure for modders to
		 * add their own biome types.</p>
		 *
		 * <p>There are <i>no</i> naming conventions besides:
		 * <ul>
		 *     <li><b>Must</b> be all upper case (enforced by name.toUpper())</li>
		 *     <li><b>No</b> Special characters. (Unenforced, just don't be a pain, if it becomes a issue I WILL
		 * make this RTE with no worry about backwards compatibility)</li>
		 * </ul></p>
		 *
		 * <p>Note: For performance's sake, the return value of this function SHOULD be cached.
		 * Two calls with the same name SHOULD return the same value.</p>
		 *
		 * @param name The name of this {@link Type}
		 * @return An instance of {@link Type} for this name.
		 */
		public static Type getType(String name, Type... subTypes) {
			name = name.toUpperCase();
			Type type = BY_NAME.get(name);

			if (type == null) {
				type = new Type(name, subTypes);
			}

			return type;
		}

		/**
		 * @return An unmodifiable collection of all current biome types.
		 */
		public static Collection<Type> getAll() {
			return ALL_TYPES;
		}

		public static Type fromVanilla(Biome.Category category) {
			if (category == Biome.Category.NONE) {
				return null;
			}

			if (category == Biome.Category.THEEND) {
				return VOID;
			}

			return getType(category.name());
		}

		/**
		 * Gets the name for this type.
		 */
		public String getName() {
			return name;
		}

		public String toString() {
			return name;
		}
	}

	private static class BiomeInfo {
		private final Set<Type> types = new HashSet<>();
		private final Set<Type> typesUnmodifiable = Collections.unmodifiableSet(this.types);
	}
}
