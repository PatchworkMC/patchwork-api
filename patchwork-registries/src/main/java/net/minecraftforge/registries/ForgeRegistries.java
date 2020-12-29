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
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleType;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.placer.BlockPlacerType;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.tree.TreeDecoratorType;
import net.minecraft.world.poi.PointOfInterestType;

/**
 * A class that exposes static references to all vanilla registries.
 * Created to have a central place to access the registries directly if modders need.
 *
 * <p>It is still advised that if you are registering things to go through
 * {@link net.minecraftforge.event.RegistryEvent registry events}, but queries and iterations can use this.</p>
 */
@SuppressWarnings("rawtypes")
public class ForgeRegistries {
	static {
		// Make sure all the registries have been setup first.
		Keys.init();
		GameData.init();
		Bootstrap.initialize();
		// TODO: Tags.init();
	}

	// Java doesn't know that the proper interfaces are implemented with mixins
	// Game objects
	public static final IForgeRegistry BLOCKS = GameData.wrap(Keys.BLOCKS, Block.class);
	public static final IForgeRegistry FLUIDS = GameData.wrap(Keys.FLUIDS, Fluid.class);
	public static final IForgeRegistry ITEMS = GameData.wrap(Keys.ITEMS, Item.class);
	// yes, EFFECTS does map to POTIONS, and POTIONS maps to POTION_TYPES
	public static final IForgeRegistry POTIONS = GameData.wrap(Keys.EFFECTS, StatusEffect.class);
	public static final IForgeRegistry SOUND_EVENTS = GameData.wrap(Keys.SOUND_EVENTS, SoundEvent.class);
	public static final IForgeRegistry POTION_TYPES = GameData.wrap(Keys.POTIONS, Potion.class);
	public static final IForgeRegistry ENCHANTMENTS = GameData.wrap(Keys.ENCHANTMENTS, Enchantment.class);
	public static final IForgeRegistry ENTITIES = GameData.wrap(Keys.ENTITY_TYPES, EntityType.class);
	public static final IForgeRegistry TILE_ENTITIES = GameData.wrap(Keys.TILE_ENTITY_TYPES, BlockEntityType.class);
	public static final IForgeRegistry PARTICLE_TYPES = GameData.wrap(Keys.PARTICLE_TYPES, ParticleType.class);
	public static final IForgeRegistry CONTAINERS = GameData.wrap(Keys.CONTAINER_TYPES, ScreenHandlerType.class);
	public static final IForgeRegistry PAINTING_TYPES = GameData.wrap(Keys.PAINTING_TYPES, PaintingMotive.class);
	public static final IForgeRegistry RECIPE_SERIALIZERS = GameData.wrap(Keys.RECIPE_SERIALIZERS, RecipeSerializer.class);
	public static final IForgeRegistry ATTRIBUTES = GameData.wrap(Keys.ATTRIBUTES, EntityAttribute.class);
	public static final IForgeRegistry STAT_TYPES = GameData.wrap(Keys.STAT_TYPES, StatType.class);

	// Villages
	public static final IForgeRegistry PROFESSIONS = GameData.wrap(Keys.VILLAGER_PROFESSIONS, VillagerProfession.class);
	public static final IForgeRegistry POI_TYPES = GameData.wrap(Keys.POI_TYPES, PointOfInterestType.class);
	public static final IForgeRegistry MEMORY_MODULE_TYPES = GameData.wrap(Keys.MEMORY_MODULE_TYPES, MemoryModuleType.class);
	public static final IForgeRegistry SENSOR_TYPES = GameData.wrap(Keys.SENSOR_TYPES, SensorType.class);
	public static final IForgeRegistry SCHEDULES = GameData.wrap(Keys.SCHEDULES, Schedule.class);
	public static final IForgeRegistry ACTIVITIES = GameData.wrap(Keys.ACTIVITIES, Activity.class);

	// Worldgen
	public static final IForgeRegistry WORLD_CARVERS = GameData.wrap(Keys.WORLD_CARVERS, Carver.class);
	public static final IForgeRegistry SURFACE_BUILDERS = GameData.wrap(Keys.SURFACE_BUILDERS, SurfaceBuilder.class);
	public static final IForgeRegistry FEATURES = GameData.wrap(Keys.FEATURES, Feature.class);
	public static final IForgeRegistry DECORATORS = GameData.wrap(Keys.DECORATORS, Decorator.class);
	public static final IForgeRegistry CHUNK_STATUS = GameData.wrap(Keys.CHUNK_STATUS, ChunkStatus.class);
	public static final IForgeRegistry STRUCTURE_FEATURES = GameData.wrap(Keys.STRUCTURE_FEATURES, StructureFeature.class);
	public static final IForgeRegistry BLOCK_STATE_PROVIDER_TYPES = GameData.wrap(Keys.BLOCK_STATE_PROVIDER_TYPES, BlockStateProviderType.class);
	public static final IForgeRegistry BLOCK_PLACER_TYPES = GameData.wrap(Keys.BLOCK_PLACER_TYPES, BlockPlacerType.class);
	public static final IForgeRegistry FOLIAGE_PLACER_TYPES = GameData.wrap(Keys.FOLIAGE_PLACER_TYPES, FoliagePlacerType.class);
	public static final IForgeRegistry TREE_DECORATOR_TYPES = GameData.wrap(Keys.TREE_DECORATOR_TYPES, TreeDecoratorType.class);
	// Dynamic/Data driven.
	//public static final IForgeRegistry BIOMES = GameData.wrap(Keys.BIOMES, Biome.class);
	// TODO: Forge Registries, unimplemented
	// DATA_SERIALIZERS, LOOT_MODIFIER_SERIALIZERS, WORLD_TYPES

	public static final class Keys {
		//Vanilla
		public static final RegistryKey<Registry<Block>> BLOCKS = key("block");
		public static final RegistryKey<Registry<Fluid>> FLUIDS = key("fluid");
		public static final RegistryKey<Registry<Item>> ITEMS = key("item");
		public static final RegistryKey<Registry<StatusEffect>> EFFECTS = key("mob_effect");
		public static final RegistryKey<Registry<Potion>> POTIONS = key("potion");
		public static final RegistryKey<Registry<EntityAttribute>> ATTRIBUTES = key("attribute");
		public static final RegistryKey<Registry<StatType<?>>> STAT_TYPES = key("stat_type");
		public static final RegistryKey<Registry<SoundEvent>> SOUND_EVENTS = key("sound_event");
		public static final RegistryKey<Registry<Enchantment>> ENCHANTMENTS = key("enchantment");
		public static final RegistryKey<Registry<EntityType<?>>> ENTITY_TYPES = key("entity_type");
		public static final RegistryKey<Registry<PaintingMotive>> PAINTING_TYPES = key("motive");
		public static final RegistryKey<Registry<ParticleType<?>>> PARTICLE_TYPES = key("particle_type");
		public static final RegistryKey<Registry<ScreenHandlerType<?>>> CONTAINER_TYPES = key("menu");
		public static final RegistryKey<Registry<BlockEntityType<?>>> TILE_ENTITY_TYPES = key("block_entity_type");
		public static final RegistryKey<Registry<RecipeSerializer<?>>> RECIPE_SERIALIZERS = key("recipe_serializer");
		public static final RegistryKey<Registry<VillagerProfession>> VILLAGER_PROFESSIONS = key("villager_profession");
		public static final RegistryKey<Registry<PointOfInterestType>> POI_TYPES = key("point_of_interest_type");
		public static final RegistryKey<Registry<MemoryModuleType<?>>> MEMORY_MODULE_TYPES = key("memory_module_type");
		public static final RegistryKey<Registry<SensorType<?>>> SENSOR_TYPES = key("sensor_type");
		public static final RegistryKey<Registry<Schedule>> SCHEDULES = key("schedule");
		public static final RegistryKey<Registry<Activity>> ACTIVITIES = key("activity");
		public static final RegistryKey<Registry<Carver<?>>> WORLD_CARVERS = key("worldgen/carver");
		public static final RegistryKey<Registry<SurfaceBuilder<?>>> SURFACE_BUILDERS = key("worldgen/surface_builder");
		public static final RegistryKey<Registry<Feature<?>>> FEATURES = key("worldgen/feature");
		public static final RegistryKey<Registry<Decorator<?>>> DECORATORS = key("worldgen/decorator");
		public static final RegistryKey<Registry<ChunkStatus>> CHUNK_STATUS = key("chunk_status");
		public static final RegistryKey<Registry<StructureFeature<?>>> STRUCTURE_FEATURES = key("worldgen/structure_feature");
		public static final RegistryKey<Registry<BlockStateProviderType<?>>> BLOCK_STATE_PROVIDER_TYPES = key("worldgen/block_state_provider_type");
		public static final RegistryKey<Registry<BlockPlacerType<?>>> BLOCK_PLACER_TYPES = key("worldgen/block_placer_type");
		public static final RegistryKey<Registry<FoliagePlacerType<?>>> FOLIAGE_PLACER_TYPES = key("worldgen/foliage_placer_type");
		public static final RegistryKey<Registry<TreeDecoratorType<?>>> TREE_DECORATOR_TYPES = key("worldgen/tree_decorator_type");
		// Vanilla Dynamic
		//public static final RegistryKey<Registry<Biome>> BIOMES = key("worldgen/biome");
		//Forge
		//public static final RegistryKey<Registry<DataSerializerEntry>> DATA_SERIALIZERS = key("data_serializers");
		//public static final RegistryKey<Registry<GlobalLootModifierSerializer<?>>> LOOT_MODIFIER_SERIALIZERS = key("forge:loot_modifier_serializers");
		//public static final RegistryKey<Registry<ForgeWorldType>> WORLD_TYPES = key("forge:world_types");

		private static <T> RegistryKey<Registry<T>> key(String name) {
			return RegistryKey.ofRegistry(new Identifier(name));
		}

		private static void init() {
			//
		}
	}

	public static void init() {
		// No-op, just so this class can get loaded.
	}
}
