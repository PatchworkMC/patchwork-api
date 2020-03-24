package net.minecraftforge.common.crafting;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;

public interface IShapedRecipe<T extends Inventory> extends Recipe<T> {
	int getRecipeWidth();

	int getRecipeHeight();
}
