package com.patchworkmc.mixin.extension;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.minecraftforge.common.crafting.IShapedRecipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeGridAligner;

@Mixin(RecipeGridAligner.class)
public interface MixinRecipeGridAligner {
	@ModifyVariable(
			method = "alignRecipeToGrid",
			at = @At("HEAD"),
			ordinal = 0
	)
	default int modifyGridWidth(int gridWidth, int gridHeight, int gridOutputSlot, Recipe<?> recipe, Iterator<?> inputs, int amount) {
		if (recipe instanceof IShapedRecipe) {
			return ((IShapedRecipe<?>) recipe).getRecipeWidth();
		}

		return gridWidth;
	}

	@ModifyVariable(
			method = "alignRecipeToGrid",
			at = @At("HEAD"),
			ordinal = 1
	)
	default int modifyGridHeight(int gridWidth, int gridHeight, int gridOutputSlot, Recipe<?> recipe, Iterator<?> inputs, int amount) {
		if (recipe instanceof IShapedRecipe) {
			return ((IShapedRecipe<?>) recipe).getRecipeHeight();
		}

		return gridHeight;
	}
}
