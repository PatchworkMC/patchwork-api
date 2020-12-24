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

package net.patchworkmc.api.enumhacks;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.function.IntFunction;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.common.util.TriPredicate;
import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CollisionView;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import net.patchworkmc.impl.enumhacks.HackableEnum;
import net.patchworkmc.impl.enumhacks.PatchworkEnchantmentTarget;
import net.patchworkmc.impl.enumhacks.PatchworkSpawnRestrictionLocation;
import net.patchworkmc.mixin.enumhacks.BannerPatternAccessor;
import net.patchworkmc.mixin.enumhacks.EntityCategoryAccessor;
import net.patchworkmc.mixin.enumhacks.OreFeatureConfigTargetAccessor;
import net.patchworkmc.mixin.enumhacks.RarityAccessor;
import net.patchworkmc.mixin.enumhacks.SpawnRestrictionLocationAccessor;
import net.patchworkmc.mixin.enumhacks.StructurePoolProjectionAccessor;

/**
 * A bunch of awful, awful hacks to implement IExtensibleEnum.
 * No, seriously. These are AWFUL hacks. Especially EnchantmentTarget.
 * @author NuclearFarts
 */
public final class EnumHacks {
	public EnumHacks() { }

	private static final EnchantmentTargetFactory ENCHANTMENT_TARGET_FACTORY;
	private static final Field ENUM_CACHE;
	private static final Field ENUM_DIRECTORY_CACHE;

	static {
		// Enum values are cached on Class objects. Store the Fields to reset the caches.
		boolean attemptDirectory = true;
		Field enumCache;

		try {
			enumCache = Class.class.getDeclaredField("enumConstants");
		} catch (NoSuchFieldException e) {
			// don't blow up quite yet. we might be on openj9.
			try {
				enumCache = Class.class.getDeclaredField("enumVars");
				attemptDirectory = false; // if we didn't go into the catch block, we're on openj9, which caches both in one object. don't look for the other one.
			} catch (NoSuchFieldException e2) {
				// we aren't on openj9 either. blow up.
				throw new RuntimeException("Problem getting enumConstants field", e);
			}
		}

		ENUM_CACHE = enumCache;
		ENUM_CACHE.setAccessible(true);

		if (attemptDirectory) {
			try {
				ENUM_DIRECTORY_CACHE = Class.class.getDeclaredField("enumConstantDirectory");
				ENUM_DIRECTORY_CACHE.setAccessible(true);
			} catch (NoSuchFieldException | SecurityException e) {
				throw new RuntimeException("Problem getting enumConstantDirectory field", e);
			}
		} else {
			ENUM_DIRECTORY_CACHE = null;
		}

		// We can't use a constructor accessor because we get around EnchantmentTarget being abstract by using EnchantmentTarget$1.
		// EnchantmentTarget$1 is a private anonymous internal class and cannot be used as a return type. Mixin doesn't like that so @Coerce won't work for some reason.
		// get a lookup that has access to EnchantmentTarget's private methods, including constructor.
		MethodHandles.Lookup lookup = ((PatchworkEnchantmentTarget) EnchantmentTarget.ALL).patchwork_getEnchantmentTargetPrivateLookup();
		MethodType type = MethodType.methodType(EnchantmentTarget.class, String.class, int.class);

		try {
			MethodHandle enchTargetCtor = lookup.findConstructor(EnchantmentTarget.ALL.getClass(), type.changeReturnType(void.class)); // ctors have void return internally
			// LambdaMetafactory stuff is technically unnecessary but it means we don't have to catch Throwable every time we instantiate an EnchantmentTarget and I'd rather not do that.
			CallSite site = LambdaMetafactory.metafactory(lookup, "create", MethodType.methodType(EnchantmentTargetFactory.class), type, enchTargetCtor, type);
			ENCHANTMENT_TARGET_FACTORY = (EnchantmentTargetFactory) site.getTarget().invoke();
		} catch (Throwable e) {
			throw new RuntimeException("Could not get EnchantmentTarget constructor/set up factory", e);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> void addToValues(T[] origArray, T newValue) {
		((HackableEnum<T>) newValue).patchwork_setValues(ArrayUtils.add(origArray, newValue));
	}

	private static void clearCachedValues(Class<? extends Enum<?>> clazz) {
		try {
			ENUM_CACHE.set(clazz, null);

			if (ENUM_DIRECTORY_CACHE != null) {
				ENUM_DIRECTORY_CACHE.set(clazz, null);
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException("Exception clearing enum cache for class " + clazz.getSimpleName(), e);
		}
	}

	public static <T extends Enum<T>> T constructAndAdd(Class<T> clazz, IntFunction<? extends T> constructor) {
		T[] values = clazz.getEnumConstants();
		T instance = constructor.apply(values.length);
		addToValues(values, instance);
		clearCachedValues(clazz);
		return instance;
	}

	public static Rarity createRarity(String name, Formatting formatting) {
		return constructAndAdd(Rarity.class, ordinal -> RarityAccessor.invokeConstructor(name, ordinal, formatting));
	}

	public static SpawnGroup createEntityCategory(String constantName, String name, int spawnCap, boolean peaceful, boolean animal) {
		return constructAndAdd(SpawnGroup.class, ordinal -> EntityCategoryAccessor.invokeConstructor(constantName, ordinal, name, spawnCap, peaceful, animal));
	}

	public static StructurePool.Projection createStructurePoolProjection(String name, String id, ImmutableList<StructureProcessor> processors) {
		StructurePool.Projection instance = constructAndAdd(StructurePool.Projection.class, ordinal -> StructurePoolProjectionAccessor.invokeConstructor(name, ordinal, id, processors));
		StructurePoolProjectionAccessor.getIdProjectionMap().put(id, instance);
		return instance;
	}

	public static OreFeatureConfig.Target createOreFeatureConfigTarget(String constantName, String name, Predicate<BlockState> predicate) {
		OreFeatureConfig.Target instance = constructAndAdd(OreFeatureConfig.Target.class, ordinal -> OreFeatureConfigTargetAccessor.invokeConstructor(constantName, ordinal, name, predicate));
		OreFeatureConfigTargetAccessor.getNameMap().put(name, instance);
		return instance;
	}

	public static BannerPattern createBannerPattern(String constantName, String name, String id, ItemStack baseStack) {
		return constructAndAdd(BannerPattern.class, ordinal -> BannerPatternAccessor.invokeConstructor(constantName, ordinal, name, id, baseStack));
	}

	public static BannerPattern createBannerPattern(String constantName, String name, String id, String recipePattern0, String recipePattern1, String recipePattern2) {
		return constructAndAdd(BannerPattern.class, ordinal -> BannerPatternAccessor.invokeConstructor(constantName, ordinal, name, id, recipePattern0, recipePattern1, recipePattern2));
	}

	public static SpawnRestriction.Location createSpawnRestrictionLocation(String name, TriPredicate<CollisionView, BlockPos, EntityType<?>> predicate) {
		SpawnRestriction.Location instance = constructAndAdd(SpawnRestriction.Location.class, ordinal -> SpawnRestrictionLocationAccessor.invokeConstructor(name, ordinal));
		((PatchworkSpawnRestrictionLocation) (Object) instance).patchwork_setPredicate(predicate);
		return instance;
	}

	public static EnchantmentTarget createEnchantmentTarget(String name, Predicate<Item> predicate) {
		EnchantmentTarget instance = constructAndAdd(EnchantmentTarget.class, ordinal -> ENCHANTMENT_TARGET_FACTORY.create(name, ordinal));
		((PatchworkEnchantmentTarget) instance).patchwork_setPredicate(predicate);
		return instance;
	}

	@FunctionalInterface
	public interface EnchantmentTargetFactory {
		EnchantmentTarget create(String target, int ordinal);
	}
}
