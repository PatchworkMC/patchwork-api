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

import net.minecraft.Bootstrap;
import net.minecraft.util.Identifier;

/**
 * A class that exposes static references to all vanilla registries.
 * Created to have a central place to access the registries directly if modders need.
 *
 * <p>It is still advised that if you are registering things to go through
 * {@link net.minecraftforge.event.RegistryEvent registry events}, but queries and iterations can use this.</p>
 */
@SuppressWarnings("rawtypes")
public class ForgeRegistries {
	// Java doesn't know that the proper interfaces are implemented with mixins.
	public static final IForgeRegistry BLOCKS;
	public static final IForgeRegistry ITEMS;
	public static final IForgeRegistry ACTIVITIES;
	public static final IForgeRegistry BIOMES;
	public static final IForgeRegistry BIOME_PROVIDER_TYPES;
	public static final IForgeRegistry TILE_ENTITIES;
	public static final IForgeRegistry WORLD_CARVERS;
	public static final IForgeRegistry CHUNK_GENERATOR_TYPES;
	public static final IForgeRegistry CHUNK_STATUS;
	public static final IForgeRegistry DECORATORS;
	public static final IForgeRegistry ENCHANTMENTS;
	public static final IForgeRegistry ENTITIES;
	public static final IForgeRegistry FEATURES;
	public static final IForgeRegistry FLUIDS;
	public static final IForgeRegistry MEMORY_MODULE_TYPES;
	public static final IForgeRegistry CONTAINERS;
	public static final IForgeRegistry POTIONS;
	public static final IForgeRegistry PAINTING_TYPES;
	public static final IForgeRegistry PARTICLE_TYPES;
	public static final IForgeRegistry POI_TYPES;
	public static final IForgeRegistry POTION_TYPES;
	public static final IForgeRegistry RECIPE_SERIALIZERS;
	public static final IForgeRegistry SCHEDULES;
	public static final IForgeRegistry SENSOR_TYPES;
	public static final IForgeRegistry SOUND_EVENTS;
	public static final IForgeRegistry STAT_TYPES;
	public static final IForgeRegistry SURFACE_BUILDERS;
	public static final IForgeRegistry PROFESSIONS;

	// TODO: Forge Registries, unimplemented
	// public static final IForgeRegistry MOD_DIMENSIONS = wrap(ModDimension.class);
	// public static final IForgeRegistry DATA_SERIALIZERS = wrap(DataSerializerEntry.class);

	static {
		// Make sure all the registries have been setup first.
		Bootstrap.initialize();

		BLOCKS = GameData.wrapVanilla(GameData.BLOCKS);
		ITEMS = GameData.wrapVanilla(GameData.ITEMS);
		ACTIVITIES = GameData.wrapVanilla(GameData.ACTIVITIES);
		BIOMES = GameData.wrapVanilla(GameData.BIOMES);
		BIOME_PROVIDER_TYPES = GameData.wrapVanilla(GameData.BIOME_PROVIDER_TYPES);
		TILE_ENTITIES = GameData.wrapVanilla(GameData.TILEENTITIES);
		WORLD_CARVERS = GameData.wrapVanilla(GameData.WORLD_CARVERS);
		CHUNK_GENERATOR_TYPES = GameData.wrapVanilla(GameData.CHUNK_GENERATOR_TYPES);
		CHUNK_STATUS = GameData.wrapVanilla(GameData.CHUNK_STATUS);
		wrap("custom_stat");
		DECORATORS = GameData.wrapVanilla(GameData.DECORATORS);
		wrap("dimension_type");
		ENCHANTMENTS = GameData.wrapVanilla(GameData.ENCHANTMENTS);
		ENTITIES = GameData.wrapVanilla(GameData.ENTITIES);
		FEATURES = GameData.wrapVanilla(GameData.FEATURES);
		FLUIDS = GameData.wrapVanilla(GameData.FLUIDS);
		MEMORY_MODULE_TYPES = GameData.wrapVanilla(GameData.MEMORY_MODULE_TYPES);
		CONTAINERS = GameData.wrapVanilla(GameData.CONTAINERS);
		POTIONS = GameData.wrapVanilla(GameData.POTIONS);
		PAINTING_TYPES = GameData.wrapVanilla(GameData.PAINTING_TYPES);
		PARTICLE_TYPES = GameData.wrapVanilla(GameData.PARTICLE_TYPES);
		POI_TYPES = GameData.wrapVanilla(GameData.POI_TYPES);
		POTION_TYPES = GameData.wrapVanilla(GameData.POTIONTYPES);
		RECIPE_SERIALIZERS = GameData.wrapVanilla(GameData.RECIPE_SERIALIZERS);
		wrap("recipe_type");
		wrap("rule_test");
		SCHEDULES = GameData.wrapVanilla(GameData.SCHEDULES);
		SENSOR_TYPES = GameData.wrapVanilla(GameData.SENSOR_TYPES);
		SOUND_EVENTS = GameData.wrapVanilla(GameData.SOUNDEVENTS);
		STAT_TYPES = GameData.wrapVanilla(GameData.STAT_TYPES);
		wrap("structure_feature");
		wrap("structure_piece");
		wrap("structure_pool_element");
		wrap("structure_processor");
		SURFACE_BUILDERS = GameData.wrapVanilla(GameData.SURFACE_BUILDERS);
		PROFESSIONS = GameData.wrapVanilla(GameData.PROFESSIONS);
		wrap("villager_type");
	}

	@SuppressWarnings("unchecked")
	private static void wrap(String vanillaName) {
		GameData.wrapVanilla(new Identifier(vanillaName));
	}

	public static void init() {
		// No-op, just so this class can get loaded.
	}
}
