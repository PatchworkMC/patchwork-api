package com.patchworkmc.impl.registries;

import java.util.HashMap;
import java.util.Map;

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

public class RegistryClassMapping {
	private static final Map<Identifier, Class> ID_TO_CLASS = new HashMap<>();
	private static final Map<Class, Identifier> CLASS_TO_ID = new HashMap<>();
	
	static {
		register(new Identifier("minecraft", "block"), Block.class);
		register(new Identifier("minecraft", "item"), Item.class);
		register(new Identifier("minecraft", "activity"), Activity.class);
		register(new Identifier("minecraft", "biome"), Biome.class);
		register(new Identifier("minecraft", "biome_source_type"), BiomeSourceType.class);
		register(new Identifier("minecraft", "block_entity_type"), BlockEntityType.class);
		register(new Identifier("minecraft", "carver"), Carver.class);
		register(new Identifier("minecraft", "chunk_generator_type"), ChunkGeneratorType.class);
		register(new Identifier("minecraft", "chunk_status"), ChunkStatus.class);
		register(new Identifier("minecraft", "custom_stat"), Identifier.class);
		register(new Identifier("minecraft", "decorator"), Decorator.class);
		register(new Identifier("minecraft", "dimension_type"), DimensionType.class);
		register(new Identifier("minecraft", "enchantment"), Enchantment.class);
		register(new Identifier("minecraft", "entity_type"), EntityType.class);
		register(new Identifier("minecraft", "feature"), Feature.class);
		register(new Identifier("minecraft", "fluid"), Fluid.class);
		register(new Identifier("minecraft", "memory_module_type"), MemoryModuleType.class);
		register(new Identifier("minecraft", "menu"), ContainerType.class);
		register(new Identifier("minecraft", "mob_effect"), StatusEffect.class);
		register(new Identifier("minecraft", "motive"), PaintingMotive.class);
		register(new Identifier("minecraft", "particle_type"), ParticleType.class);
		register(new Identifier("minecraft", "point_of_interest_type"), PointOfInterestType.class);
		register(new Identifier("minecraft", "potion"), Potion.class);
		register(new Identifier("minecraft", "recipe_serializer"), RecipeSerializer.class);
		register(new Identifier("minecraft", "recipe_type"), RecipeType.class);
		register(new Identifier("minecraft", "rule_test"), RuleTest.class);
		register(new Identifier("minecraft", "schedule"), Schedule.class);
		register(new Identifier("minecraft", "sensor_type"), SensorType.class);
		register(new Identifier("minecraft", "sound_event"), SoundEvent.class);
		register(new Identifier("minecraft", "stat_type"), StatType.class);
		register(new Identifier("minecraft", "structure_feature"), StructureFeature.class);
		register(new Identifier("minecraft", "structure_piece"), StructurePieceType.class);
		register(new Identifier("minecraft", "structure_pool_element"), StructurePoolElementType.class);
		register(new Identifier("minecraft", "structure_processor"), StructureProcessorType.class);
		register(new Identifier("minecraft", "surface_builder"), SurfaceBuilder.class);
		register(new Identifier("minecraft", "villager_profession"), VillagerProfession.class);
		register(new Identifier("minecraft", "villager_type"), VillagerType.class);
	}

	private static void register(Identifier identifier, Class clazz) {
		ID_TO_CLASS.put(identifier, clazz);
		CLASS_TO_ID.put(clazz, identifier);
	}

	public static Class<?> getClass(Identifier identifier) {
		return ID_TO_CLASS.get(identifier);
	}

	public static Identifier getIdentifier(Class<?> clazz) {
		Identifier existing = CLASS_TO_ID.get(clazz);

		if (existing != null) {
			return existing;
		}

		Class<?> superclass = clazz.getSuperclass();

		if(superclass == null || superclass == Object.class) {
			return null;
		} else {
			return getIdentifier(superclass);
		}
	}
}
