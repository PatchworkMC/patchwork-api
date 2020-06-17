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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.ModLoadingContext;

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
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
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
import net.minecraft.world.poi.PointOfInterestType;

public class GameData {
	// These are needed because some Forge mods access these static public fields
	// Vanilla registries

	// Game objects
	public static final Identifier BLOCKS = new Identifier("block");
	public static final Identifier FLUIDS = new Identifier("fluid");
	public static final Identifier ITEMS = new Identifier("item");
	public static final Identifier POTIONS = new Identifier("mob_effect");
	public static final Identifier BIOMES = new Identifier("biome");
	public static final Identifier SOUNDEVENTS = new Identifier("sound_event");
	public static final Identifier POTIONTYPES = new Identifier("potion");
	public static final Identifier ENCHANTMENTS = new Identifier("enchantment");
	public static final Identifier ENTITIES = new Identifier("entity_type");
	public static final Identifier TILEENTITIES = new Identifier("block_entity_type");
	public static final Identifier PARTICLE_TYPES = new Identifier("particle_type");
	public static final Identifier CONTAINERS = new Identifier("menu");
	public static final Identifier PAINTING_TYPES = new Identifier("motive"); // sic
	public static final Identifier RECIPE_SERIALIZERS = new Identifier("recipe_serializer");
	public static final Identifier STAT_TYPES = new Identifier("stat_type");

	// Villages
	public static final Identifier PROFESSIONS = new Identifier("villager_profession");
	public static final Identifier POI_TYPES = new Identifier("point_of_interest_type");
	public static final Identifier MEMORY_MODULE_TYPES = new Identifier("memory_module_type");
	public static final Identifier SENSOR_TYPES = new Identifier("sensor_type");
	public static final Identifier SCHEDULES = new Identifier("schedule");
	public static final Identifier ACTIVITIES = new Identifier("activity");

	// Worldgen
	public static final Identifier WORLD_CARVERS = new Identifier("carver");
	public static final Identifier SURFACE_BUILDERS = new Identifier("surface_builder");
	public static final Identifier FEATURES = new Identifier("feature");
	public static final Identifier DECORATORS = new Identifier("decorator");
	public static final Identifier BIOME_PROVIDER_TYPES = new Identifier("biome_source_type");
	public static final Identifier CHUNK_GENERATOR_TYPES = new Identifier("chunk_generator_type");
	public static final Identifier CHUNK_STATUS = new Identifier("chunk_status");

	// TODO: Custom forge registries
	// public static final Identifier MODDIMENSIONS = new
	// Identifier("forge:moddimensions");
	// public static final Identifier SERIALIZERS = new
	// Identifier("minecraft:dataserializers");
	// public static final Identifier LOOT_MODIFIER_SERIALIZERS = new
	// Identifier("forge:loot_modifier_serializers");

	private static final Map<Identifier, RegistryBuilder<?>> vanillaWrapperBuilders = new HashMap<>();
	private static final Logger LOGGER = LogManager.getLogger();

	static {
		// Game objects
		wrap(BLOCKS, Block.class);
		wrap(FLUIDS, Fluid.class);
		wrap(ITEMS, Item.class);
		wrap(POTIONS, StatusEffect.class);
		wrap(BIOMES, Biome.class);
		wrap(SOUNDEVENTS, SoundEvent.class);
		wrap(POTIONTYPES, Potion.class);
		wrap(ENCHANTMENTS, Enchantment.class);
		wrap(ENTITIES, EntityType.class);
		wrap(TILEENTITIES, BlockEntityType.class);
		wrap(PARTICLE_TYPES, ParticleType.class);
		wrap(CONTAINERS, ContainerType.class);
		wrap(PAINTING_TYPES, PaintingMotive.class);
		wrap(RECIPE_SERIALIZERS, RecipeSerializer.class);
		wrap(STAT_TYPES, StatType.class);

		// Villages
		wrap(PROFESSIONS, VillagerProfession.class);
		wrap(POI_TYPES, PointOfInterestType.class);
		wrap(MEMORY_MODULE_TYPES, MemoryModuleType.class);
		wrap(SENSOR_TYPES, SensorType.class);
		wrap(SCHEDULES, Schedule.class);
		wrap(ACTIVITIES, Activity.class);

		// Worldgen
		wrap(WORLD_CARVERS, Carver.class);
		wrap(SURFACE_BUILDERS, SurfaceBuilder.class);
		wrap(FEATURES, Feature.class);
		wrap(DECORATORS, Decorator.class);
		wrap(BIOME_PROVIDER_TYPES, BiomeSourceType.class);
		wrap(CHUNK_GENERATOR_TYPES, ChunkGeneratorType.class);
		wrap(CHUNK_STATUS, ChunkStatus.class);

		// TODO: Patchwork Project, check these
		wrap("dimension_type", DimensionType.class);
		wrap("custom_stat", Identifier.class);
		wrap("recipe_type", RecipeType.class);
		wrap("rule_test", RuleTestType.class);
		wrap("structure_feature", StructureFeature.class);
		wrap("structure_piece", StructurePieceType.class);
		wrap("structure_pool_element", StructurePoolElementType.class);
		wrap("structure_processor", StructureProcessorType.class);
		wrap("villager_type", VillagerType.class);
	}

	private static void wrap(String name, Class superClazz) {
		wrap(new Identifier(name), superClazz);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static RegistryBuilder<?> wrap(Identifier id, Class superClazz) {
		RegistryBuilder builder = new RegistryBuilder();
		builder.setName(id);
		builder.setType(superClazz);
		builder.disableOverrides();	// Vanilla registry does not support override, modification is disabled by default
		vanillaWrapperBuilders.put(id, builder);
		return builder;
	}

	/**
	 * Check a name for a domain prefix, and if not present infer it from the
	 * current active mod container.
	 *
	 * @param name          The name or resource location
	 * @param warnOverrides If false, the prefix is forcefully updated without a warning,
	 *                      and if true, the prefix is not updated and there is just a
	 *                      warning message.
	 * @return The {@link Identifier} with given or inferred domain
	 */
	public static Identifier checkPrefix(String name, boolean warnOverrides) {
		int colonIndex = name.lastIndexOf(':');

		if (colonIndex == -1) {
			String prefix = ModLoadingContext.get().getActiveNamespace();

			return new Identifier(prefix, name);
		}

		String oldPrefix = name.substring(0, colonIndex).toLowerCase(Locale.ROOT);

		String newName = name.substring(colonIndex + 1);
		String prefix = ModLoadingContext.get().getActiveNamespace();

		if (warnOverrides && !oldPrefix.equals(prefix) && oldPrefix.length() > 0) {
			LOGGER.info("Potentially Dangerous alternative prefix `{}` for name `{}`, expected `{}`. This could be a intended override, but in most cases indicates a broken mod.", oldPrefix, name, prefix);
			prefix = oldPrefix;
		}

		return new Identifier(prefix, newName);
	}

	@SuppressWarnings("unchecked")
	public static <V extends IForgeRegistryEntry<V>> IForgeRegistry<V> wrapVanilla(Identifier identifier, Registry<?> registry) {
		RegistryBuilder<V> builder = (RegistryBuilder<V>) vanillaWrapperBuilders.get(identifier);

		if (builder == null) {
			LOGGER.warn("Detected an unknown Vanilla registry with no Patchwork equivalent: %s", identifier);
			return null;
		}

		if (StructureFeature.class.isAssignableFrom(builder.getType())) {
			// In Forge mods, StructureFeatures are registered with IForgeRegistry<Feature>
			// There is no such thing like IForgeRegistry<StructureFeature>, so simply return null here.
			return null;
		}

		builder.setVanillaRegistry((Registry<V>) registry);
		return builder.create();
	}
}
