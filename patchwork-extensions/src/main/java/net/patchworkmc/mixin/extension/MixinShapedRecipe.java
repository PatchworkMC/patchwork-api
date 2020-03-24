package net.patchworkmc.mixin.extension;

import net.minecraftforge.common.crafting.IShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.recipe.ShapedRecipe;

@Mixin(ShapedRecipe.class)
public abstract class MixinShapedRecipe implements IShapedRecipe<CraftingInventory> {
	@Shadow
	public abstract int getWidth();

	@Shadow
	public abstract int getHeight();

	@Override
	public int getRecipeWidth() {
		return getWidth();
	}

	@Override
	public int getRecipeHeight() {
		return getHeight();
	}
}
