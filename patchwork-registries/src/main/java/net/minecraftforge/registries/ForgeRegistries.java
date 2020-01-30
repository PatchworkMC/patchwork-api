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
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.container.ContainerType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleType;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.StatType;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

import com.patchworkmc.impl.registries.RegistryClassMapping;
import com.patchworkmc.impl.registries.RegistryEventDispatcher;

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

	// TODO: Forge Registries, when these are implemented
	// public static final IForgeRegistry MOD_DIMENSIONS = wrap(ModDimension.class);
	// public static final IForgeRegistry DATA_SERIALIZERS = wrap(DataSerializerEntry.class);

	static {
		// Make sure all the registries have been setup first.
		Bootstrap.initialize();

		BLOCKS = wrap("block", Block.class);
		ITEMS = wrap("item", Item.class);
		ACTIVITIES = wrap("activity", Activity.class);
		BIOMES = wrap("biome", Biome.class);
		BIOME_PROVIDER_TYPES = wrap("biome_source_type", BiomeSourceType.class);
		TILE_ENTITIES = wrap("block_entity_type", BlockEntityType.class);
		WORLD_CARVERS = wrap("carver", Carver.class);
		CHUNK_GENERATOR_TYPES = wrap("chunk_generator_type", ChunkGeneratorType.class);
		CHUNK_STATUS = wrap("chunk_status", ChunkStatus.class);
		wrap("custom_stat", Identifier.class);
		DECORATORS = wrap("decorator", Decorator.class);
		wrap("dimension_type", DimensionType.class);
		ENCHANTMENTS = wrap("enchantment", Enchantment.class);
		ENTITIES = wrap("entity_type", EntityType.class);
		FEATURES = wrap("feature", Feature.class);
		FLUIDS = wrap("fluid", Fluid.class);
		MEMORY_MODULE_TYPES = wrap("memory_module_type", MemoryModuleType.class);
		CONTAINERS = wrap("menu", ContainerType.class);
		POTIONS = wrap("mob_effect", StatusEffect.class);
		PAINTING_TYPES = wrap("motive", PaintingMotive.class);
		PARTICLE_TYPES = wrap("particle_type", ParticleType.class);
		POI_TYPES = wrap("point_of_interest_type", PointOfInterestType.class);
		POTION_TYPES = wrap("potion", Potion.class);
		RECIPE_SERIALIZERS = wrap("recipe_serializer", RecipeSerializer.class);
		wrap("recipe_type", RecipeType.class);
		wrap("rule_test", RuleTest.class);
		SCHEDULES = wrap("schedule", Schedule.class);
		SENSOR_TYPES = wrap("sensor_type", SensorType.class);
		SOUND_EVENTS = wrap("sound_event", SoundEvent.class);
		STAT_TYPES = wrap("stat_type", StatType.class);
		wrap("structure_feature", StructureFeature.class);
		wrap("structure_piece", StructurePieceType.class);
		wrap("structure_pool_element", StructurePoolElementType.class);
		wrap("structure_processor", StructureProcessorType.class);
		SURFACE_BUILDERS = wrap("surface_builder", SurfaceBuilder.class);
		PROFESSIONS = wrap("villager_profession", VillagerProfession.class);
		wrap("villager_type", VillagerType.class);
	}

	@SuppressWarnings("unchecked")
	private static <T> IForgeRegistry wrap(String name, Class superClazz) {
		Identifier identifier = new Identifier("minecraft", name);
		Registry registry = Registry.REGISTRIES.get(identifier);

		ForgeRegistry wrapped = new ForgeRegistry(identifier, registry, superClazz);

		RegistryClassMapping.register(wrapped);
		RegistryManager.ACTIVE.addRegistry(identifier, wrapped);
		RegistryEventDispatcher.register(wrapped);

		return wrapped;
	}

	public static void init() {
		// No-op, just so this class can get loaded.
	}
}
