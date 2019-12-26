package net.coderbot.patchwork;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
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
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.registries.ForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Patchwork implements ModInitializer {
	private static final Map<Identifier, Class> SUPERS = new HashMap<>();
	private static final Logger LOGGER = LogManager.getLogger(Patchwork.class);

	static {
		SUPERS.put(new Identifier("minecraft", "block"), Block.class);
		SUPERS.put(new Identifier("minecraft", "item"), Item.class);
		SUPERS.put(new Identifier("minecraft", "activity"), Activity.class);
		SUPERS.put(new Identifier("minecraft", "biome"), Biome.class);
		SUPERS.put(new Identifier("minecraft", "biome_source_type"), BiomeSourceType.class);
		SUPERS.put(new Identifier("minecraft", "block_entity_type"), BlockEntityType.class);
		SUPERS.put(new Identifier("minecraft", "carver"), Carver.class);
		SUPERS.put(new Identifier("minecraft", "chunk_generator_type"), ChunkGeneratorType.class);
		SUPERS.put(new Identifier("minecraft", "chunk_status"), ChunkStatus.class);
		SUPERS.put(new Identifier("minecraft", "custom_stat"), Identifier.class);
		SUPERS.put(new Identifier("minecraft", "decorator"), Decorator.class);
		SUPERS.put(new Identifier("minecraft", "dimension_type"), DimensionType.class);
		SUPERS.put(new Identifier("minecraft", "enchantment"), Enchantment.class);
		SUPERS.put(new Identifier("minecraft", "entity_type"), EntityType.class);
		SUPERS.put(new Identifier("minecraft", "feature"), Feature.class);
		SUPERS.put(new Identifier("minecraft", "fluid"), Fluid.class);
		SUPERS.put(new Identifier("minecraft", "memory_module_type"), MemoryModuleType.class);
		SUPERS.put(new Identifier("minecraft", "menu"), ContainerType.class);
		SUPERS.put(new Identifier("minecraft", "mob_effect"), StatusEffect.class);
		SUPERS.put(new Identifier("minecraft", "motive"), PaintingMotive.class);
		SUPERS.put(new Identifier("minecraft", "particle_type"), ParticleType.class);
		SUPERS.put(new Identifier("minecraft", "point_of_interest_type"), PointOfInterestType.class);
		SUPERS.put(new Identifier("minecraft", "potion"), Potion.class);
		SUPERS.put(new Identifier("minecraft", "recipe_serializer"), RecipeSerializer.class);
		SUPERS.put(new Identifier("minecraft", "recipe_type"), RecipeType.class);
		SUPERS.put(new Identifier("minecraft", "rule_test"), RuleTest.class);
		SUPERS.put(new Identifier("minecraft", "schedule"), Schedule.class);
		SUPERS.put(new Identifier("minecraft", "sensor_type"), SensorType.class);
		SUPERS.put(new Identifier("minecraft", "sound_event"), SoundEvent.class);
		SUPERS.put(new Identifier("minecraft", "stat_type"), StatType.class);
		SUPERS.put(new Identifier("minecraft", "structure_feature"), StructureFeature.class);
		SUPERS.put(new Identifier("minecraft", "structure_piece"), StructurePieceType.class);
		SUPERS.put(new Identifier("minecraft", "structure_pool_element"), StructurePoolElementType.class);
		SUPERS.put(new Identifier("minecraft", "structure_processor"), StructureProcessorType.class);
		SUPERS.put(new Identifier("minecraft", "surface_builder"), SurfaceBuilder.class);
		SUPERS.put(new Identifier("minecraft", "villager_profession"), VillagerProfession.class);
		SUPERS.put(new Identifier("minecraft", "villager_type"), VillagerType.class);
	}

	private static void dispatchRegistryEvents(Map<ForgeInitializer, FMLModContainer> mods) {
		// verify supers

		List<Identifier> registries = new ArrayList<>(Registry.REGISTRIES.getIds());

		registries.remove(Registry.REGISTRIES.getId(Registry.BLOCK));
		registries.remove(Registry.REGISTRIES.getId(Registry.ITEM));

		registries.sort((o1, o2) -> String.valueOf(o1).compareToIgnoreCase(String.valueOf(o2)));

		registries.add(0, Registry.REGISTRIES.getId(Registry.BLOCK));
		registries.add(1, Registry.REGISTRIES.getId(Registry.ITEM));

		LOGGER.info("Dispatching registry events for: " + registries);

		for (Identifier identifier : registries) {
			Registry registry = Registry.REGISTRIES.get(identifier);
			Class superType = SUPERS.get(identifier);

			ForgeRegistry forgeRegistry = new ForgeRegistry(identifier, registry, superType);

			// Note: this checks to see if supers is correct
			/*for(Map.Entry<Identifier, Object> entry: (Set<Map.Entry<Identifier, Object>>)forgeRegistry.getEntries()) {
				if(!superType.isAssignableFrom(entry.getValue().getClass())) {
					System.err.println("Bad registry type for " + identifier + " (" + entry.getKey() + ")");
					throw new RuntimeException();
				}
			}*/

			dispatch(mods, new RegistryEvent.Register(forgeRegistry));
		}
	}

	private static void dispatch(Map<ForgeInitializer, FMLModContainer> mods, Event event) {
		for (Map.Entry<ForgeInitializer, FMLModContainer> entry : mods.entrySet()) {
			ForgeInitializer initializer = entry.getKey();
			FMLModContainer container = entry.getValue();

			ModLoadingContext.get().setActiveContainer(new ModContainer(initializer.getModId()), new FMLJavaModLoadingContext(container));

			container.getEventBus().post(event);

			ModLoadingContext.get().setActiveContainer(null, "minecraft");
		}
	}

	@Override
	public void onInitialize() {
		Map<ForgeInitializer, FMLModContainer> mods = new HashMap<>();

		// Construct forge mods

		for (ForgeInitializer initializer : FabricLoader.getInstance().getEntrypoints("patchwork", ForgeInitializer.class)) {
			LOGGER.info("Constructing Forge mod: " + initializer);

			FMLModContainer container = new FMLModContainer();
			ModLoadingContext.get().setActiveContainer(new ModContainer(initializer.getModId()), new FMLJavaModLoadingContext(container));

			initializer.onForgeInitialize();

			ModLoadingContext.get().setActiveContainer(null, "minecraft");

			mods.put(initializer, container);
		}

		// Send initialization events

		dispatchRegistryEvents(mods);
		dispatch(mods, new FMLCommonSetupEvent(new ModContainer("minecraft"))); // TODO: One per modcontainer
		dispatch(mods, new FMLLoadCompleteEvent(new ModContainer("minecraft"))); // TODO: Ditto
	}
}
