package net.patchworkmc.mixin.levelgenerators;

import java.util.function.LongFunction;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.common.extensions.IForgeWorldType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.biome.layer.AddClimateLayers;
import net.minecraft.world.biome.layer.AddColdClimatesLayer;
import net.minecraft.world.biome.layer.AddDeepOceanLayer;
import net.minecraft.world.biome.layer.AddEdgeBiomesLayer;
import net.minecraft.world.biome.layer.AddHillsLayer;
import net.minecraft.world.biome.layer.AddIslandLayer;
import net.minecraft.world.biome.layer.AddMushroomIslandLayer;
import net.minecraft.world.biome.layer.AddRiversLayer;
import net.minecraft.world.biome.layer.AddSunflowerPlainsLayer;
import net.minecraft.world.biome.layer.ApplyOceanTemperatureLayer;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.CellScaleLayer;
import net.minecraft.world.biome.layer.ContinentLayer;
import net.minecraft.world.biome.layer.IncreaseEdgeCurvatureLayer;
import net.minecraft.world.biome.layer.NoiseToRiverLayer;
import net.minecraft.world.biome.layer.OceanTemperatureLayer;
import net.minecraft.world.biome.layer.ScaleLayer;
import net.minecraft.world.biome.layer.SimpleLandNoiseLayer;
import net.minecraft.world.biome.layer.SmoothenShorelineLayer;
import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;

import net.patchworkmc.impl.levelgenerators.PatchworkGeneratorType;

@Mixin(BiomeLayers.class)
public class MixinBiomeLayers {
	@Shadow
	private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> stack(long seed, ParentedLayer layer, LayerFactory<T> parent, int count, LongFunction<C> contextProvider) {
		throw new RuntimeException("Failed to shadow BiomeLayers#stack!");
	}

	@Inject(at = @At("HEAD"), method = "build", cancellable = true)
	private static <T extends LayerSampler, C extends LayerSampleContext<T>> void build(LevelGeneratorType generatorType, OverworldChunkGeneratorConfig settings, LongFunction<C> contextProvider, CallbackInfoReturnable<ImmutableList<LayerFactory<T>>> info) {
		if (generatorType instanceof PatchworkGeneratorType) {
			LayerFactory<T> continentLayer = ContinentLayer.INSTANCE.create(contextProvider.apply(1L));
			continentLayer = ScaleLayer.FUZZY.create(contextProvider.apply(2000L), continentLayer);
			continentLayer = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(1L), continentLayer);
			continentLayer = ScaleLayer.NORMAL.create(contextProvider.apply(2001L), continentLayer);
			continentLayer = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(2L), continentLayer);
			continentLayer = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(50L), continentLayer);
			continentLayer = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(70L), continentLayer);
			continentLayer = AddIslandLayer.INSTANCE.create(contextProvider.apply(2L), continentLayer);
			LayerFactory<T> layerFactory2 = OceanTemperatureLayer.INSTANCE.create(contextProvider.apply(2L));
			layerFactory2 = stack(2001L, ScaleLayer.NORMAL, layerFactory2, 6, contextProvider);
			continentLayer = AddColdClimatesLayer.INSTANCE.create(contextProvider.apply(2L), continentLayer);
			continentLayer = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(3L), continentLayer);
			continentLayer = AddClimateLayers.AddTemperateBiomesLayer.INSTANCE.create(contextProvider.apply(2L), continentLayer);
			continentLayer = AddClimateLayers.AddCoolBiomesLayer.INSTANCE.create(contextProvider.apply(2L), continentLayer);
			continentLayer = AddClimateLayers.AddSpecialBiomesLayer.INSTANCE.create(contextProvider.apply(3L), continentLayer);
			continentLayer = ScaleLayer.NORMAL.create(contextProvider.apply(2002L), continentLayer);
			continentLayer = ScaleLayer.NORMAL.create(contextProvider.apply(2003L), continentLayer);
			continentLayer = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(4L), continentLayer);
			continentLayer = AddMushroomIslandLayer.INSTANCE.create(contextProvider.apply(5L), continentLayer);
			continentLayer = AddDeepOceanLayer.INSTANCE.create(contextProvider.apply(4L), continentLayer);
			continentLayer = stack(1000L, ScaleLayer.NORMAL, continentLayer, 0, contextProvider);
			int biomeSize = 4;
			int riverSize = biomeSize;

			if (settings != null) {
				biomeSize = settings.getBiomeSize();
				riverSize = settings.getRiverSize();
			}

			if (generatorType == LevelGeneratorType.LARGE_BIOMES) {
				biomeSize = 6;
			}

			LayerFactory<T> riverLayer = stack(1000L, ScaleLayer.NORMAL, continentLayer, 0, contextProvider);
			riverLayer = SimpleLandNoiseLayer.INSTANCE.create(contextProvider.apply(100L), riverLayer);
			LayerFactory<T> noiseLayer = ((IForgeWorldType) generatorType).getBiomeLayer(continentLayer, settings, contextProvider);
			LayerFactory<T> layerFactory5 = stack(1000L, ScaleLayer.NORMAL, riverLayer, 2, contextProvider);
			noiseLayer = AddHillsLayer.INSTANCE.create(contextProvider.apply(1000L), noiseLayer, layerFactory5);
			riverLayer = stack(1000L, ScaleLayer.NORMAL, riverLayer, 2, contextProvider);
			riverLayer = stack(1000L, ScaleLayer.NORMAL, riverLayer, riverSize, contextProvider);
			riverLayer = NoiseToRiverLayer.INSTANCE.create(contextProvider.apply(1L), riverLayer);
			riverLayer = SmoothenShorelineLayer.INSTANCE.create(contextProvider.apply(1000L), riverLayer);
			noiseLayer = AddSunflowerPlainsLayer.INSTANCE.create(contextProvider.apply(1001L), noiseLayer);

			for (int k = 0; k < biomeSize; ++k) {
				noiseLayer = ScaleLayer.NORMAL.create(contextProvider.apply((long) (1000 + k)), noiseLayer);

				if (k == 0) {
					noiseLayer = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(3L), noiseLayer);
				}

				if (k == 1 || biomeSize == 1) {
					noiseLayer = AddEdgeBiomesLayer.INSTANCE.create(contextProvider.apply(1000L), noiseLayer);
				}
			}

			noiseLayer = SmoothenShorelineLayer.INSTANCE.create(contextProvider.apply(1000L), noiseLayer);
			noiseLayer = AddRiversLayer.INSTANCE.create(contextProvider.apply(100L), noiseLayer, riverLayer);
			noiseLayer = ApplyOceanTemperatureLayer.INSTANCE.create(contextProvider.apply(100L), noiseLayer, layerFactory2);
			LayerFactory<T> biomeLayer = CellScaleLayer.INSTANCE.create(contextProvider.apply(10L), noiseLayer);
			info.setReturnValue(ImmutableList.of(noiseLayer, biomeLayer, noiseLayer));
		}
	}
}
