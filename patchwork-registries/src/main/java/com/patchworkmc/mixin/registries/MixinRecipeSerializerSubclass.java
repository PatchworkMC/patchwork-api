package com.patchworkmc.mixin.registries;

import net.minecraftforge.registries.IForgeRegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.recipe.CuttingRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import com.patchworkmc.impl.registries.ExtendedForgeRegistryEntry;
import com.patchworkmc.impl.registries.Identifiers;

@Mixin({ShapedRecipe.Serializer.class, ShapelessRecipe.Serializer.class, CookingRecipeSerializer.class, CuttingRecipe.Serializer.class, SpecialRecipeSerializer.class})
public class MixinRecipeSerializerSubclass implements ExtendedForgeRegistryEntry<RecipeSerializer> {
	@Unique
	private Identifier registryName;

	@Override
	public IForgeRegistryEntry<RecipeSerializer> setRegistryName(Identifier name) {
		this.registryName = name;

		return this;
	}

	public Identifier getRegistryName() {
		RecipeSerializer<?> recipeSerializer = (RecipeSerializer<?>) this;

		return Identifiers.getOrFallback(Registry.RECIPE_SERIALIZER, recipeSerializer, registryName);
	}

	public Class<RecipeSerializer> getRegistryType() {
		return RecipeSerializer.class;
	}
}
