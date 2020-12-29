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

package net.patchworkmc.mixin.registries;

import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.block.AbstractBlock;
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
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
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

import net.patchworkmc.impl.registries.ExtendedForgeRegistryEntry;
import net.patchworkmc.impl.registries.Identifiers;

@Mixin({
		AbstractBlock.class, Fluid.class, Item.class, StatusEffect.class, SoundEvent.class, Potion.class, Enchantment.class,
		EntityType.class, BlockEntityType.class, ParticleType.class, ScreenHandlerType.class, PaintingMotive.class,
		EntityAttribute.class, StatType.class, VillagerProfession.class,
		PointOfInterestType.class, MemoryModuleType.class, SensorType.class, Schedule.class, Activity.class,
		Carver.class, SurfaceBuilder.class, Feature.class, Decorator.class, ChunkStatus.class, StructureFeature.class,
		BlockStateProviderType.class, BlockPlacerType.class, FoliagePlacerType.class, TreeDecoratorType.class /* TODO biome */
})
public class MixinAllForgeRegistryEntries implements ExtendedForgeRegistryEntry {
	@Unique
	private Identifier registryName;
	@Unique
	private Class<?> registryType;

	@Override
	public IForgeRegistryEntry setRegistryName(Identifier name) {
		this.registryName = name;

		return this;
	}

	public Identifier getRegistryName() {
		return Identifiers.getOrFallback(getRegistryType(), this, registryName);
	}

	public Class<?> getRegistryType() {
		// TODO: This works for now, but it's probably unacceptably slow for big packs.
		//  Then again, knowing Forge, maybe not.
		if (registryType != null) {
			return registryType;
		}

		Class<?> target = getClass();

		while (target != Object.class) {
			RegistryKey<?> key = GameData.patchwork$REGISTRY_MAP.get(target);

			if (key == null) {
				target = target.getSuperclass();
			} else {
				break;
			}
		}

		if (target == Object.class) {
			throw new IllegalStateException("Registry type not in our map?");
		}

		registryType = target;
		return target;
	}
}
