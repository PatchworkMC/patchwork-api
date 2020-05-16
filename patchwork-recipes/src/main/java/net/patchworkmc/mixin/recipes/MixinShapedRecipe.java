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

package net.patchworkmc.mixin.recipes;

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
