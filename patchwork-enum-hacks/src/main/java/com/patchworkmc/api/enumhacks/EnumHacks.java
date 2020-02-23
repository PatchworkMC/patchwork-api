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

package com.patchworkmc.api.enumhacks;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.ArrayUtils;
import net.minecraftforge.common.util.TriPredicate;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import com.patchworkmc.impl.enumhacks.HackableEnum;
import com.patchworkmc.impl.enumhacks.PatchworkEnchantmentTarget;
import com.patchworkmc.impl.enumhacks.PatchworkSpawnRestrictionLocation;
import com.patchworkmc.mixin.enumhacks.BannerPatternAccessor;
import com.patchworkmc.mixin.enumhacks.EntityCategoryAccessor;
import com.patchworkmc.mixin.enumhacks.OreFeatureConfigTargetAccessor;
import com.patchworkmc.mixin.enumhacks.RarityAccessor;
import com.patchworkmc.mixin.enumhacks.SpawnRestrictionLocationAccessor;
import com.patchworkmc.mixin.enumhacks.StructurePoolProjectionAccessor;

/**
 * A bunch of awful, awful hacks to implement IExtensibleEnum.
 * No, seriously. These are AWFUL hacks. Especially EnchantmentTarget.
 * @author NuclearFarts
 */
public final class EnumHacks {
	private EnumHacks() { }

	public static final EnchantmentTargetFactory ENCHANTMENT_TARGET_FACTORY;

	/*
	 * We can't use a constructor accessor because we get around EnchantmentTarget being abstract by using EnchantmentTarget$1.
	 * EnchantmentTarget$1 is a private anonymous internal class and cannot be used as a return type. Mixin doesn't like that so @Coerce won't work for some reason.
	 */
	static {
		//get a lookup that has access to EnchantmentTarget's private methods, including constructor.
		MethodHandles.Lookup lookup = ((PatchworkEnchantmentTarget) EnchantmentTarget.ALL).patchwork_getEnchantmentTargetPrivateLookup();
		MethodType type = MethodType.methodType(EnchantmentTarget.class, String.class, int.class);

		try {
			MethodHandle enchTargetCtor = lookup.findConstructor(EnchantmentTarget.ALL.getClass(), type.changeReturnType(void.class)); //ctors have void return internally
			//LambdaMetafactory stuff is technically unnecessary but it means we don't have to catch Throwable every time we instantiate an EnchantmentTarget and I'd rather not do that.
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

	public static Rarity createRarity(String name, Formatting formatting) {
		Rarity[] values = Rarity.values(); //each values call creates a copy of the array. avoid them.
		Rarity instance = RarityAccessor.invokeConstructor(name, values.length, formatting);
		addToValues(values, instance);
		return instance;
	}

	public static EntityCategory createEntityCategory(String constantName, String name, int spawnCap, boolean peaceful, boolean animal) {
		EntityCategory[] values = EntityCategory.values();
		EntityCategory instance = EntityCategoryAccessor.invokeConstructor(constantName, values.length, name, spawnCap, peaceful, animal);
		addToValues(values, instance);
		return instance;
	}

	public static StructurePool.Projection createStructurePoolProjection(String name, String id, ImmutableList<StructureProcessor> processors) {
		StructurePool.Projection[] values = StructurePool.Projection.values();
		StructurePool.Projection instance = StructurePoolProjectionAccessor.invokeConstructor(name, values.length, id, processors);
		addToValues(values, instance);
		StructurePoolProjectionAccessor.getIdProjectionMap().put(id, instance);
		return instance;
	}

	public static OreFeatureConfig.Target createOreFeatureConfigTarget(String constantName, String name, Predicate<BlockState> predicate) {
		OreFeatureConfig.Target[] values = OreFeatureConfig.Target.values();
		OreFeatureConfig.Target instance = OreFeatureConfigTargetAccessor.invokeConstructor(constantName, values.length, name, predicate);
		addToValues(values, instance);
		OreFeatureConfigTargetAccessor.getNameMap().put(name, instance);
		return instance;
	}

	public static BannerPattern createBannerPattern(String constantName, String name, String id, ItemStack baseStack) {
		BannerPattern[] values = BannerPattern.values();
		BannerPattern instance = BannerPatternAccessor.invokeConstructor(constantName, values.length, name, id, baseStack);
		addToValues(values, instance);
		return instance;
	}

	public static BannerPattern createBannerPattern(String constantName, String name, String id, String recipePattern0, String recipePattern1, String recipePattern2) {
		BannerPattern[] values = BannerPattern.values();
		BannerPattern instance = BannerPatternAccessor.invokeConstructor(constantName, values.length, name, id, recipePattern0, recipePattern1, recipePattern2);
		addToValues(values, instance);
		return instance;
	}

	public static SpawnRestriction.Location createSpawnRestrictionLocation(String name, TriPredicate<ViewableWorld, BlockPos, EntityType<?>> predicate) {
		SpawnRestriction.Location[] values = SpawnRestriction.Location.values();
		SpawnRestriction.Location instance = SpawnRestrictionLocationAccessor.invokeConstructor(name, values.length);
		((PatchworkSpawnRestrictionLocation) (Object) instance).patchwork_setPredicate(predicate);
		addToValues(values, instance);
		return instance;
	}

	public static EnchantmentTarget createEnchantmentTarget(String name, Predicate<Item> predicate) {
		EnchantmentTarget[] values = EnchantmentTarget.values();
		EnchantmentTarget instance = ENCHANTMENT_TARGET_FACTORY.create(name, values.length);
		((PatchworkEnchantmentTarget) instance).patchwork_setPredicate(predicate);
		addToValues(values, instance);
		return instance;
	}

	@FunctionalInterface
	public interface EnchantmentTargetFactory {
		EnchantmentTarget create(String target, int ordinal);
	}
}
