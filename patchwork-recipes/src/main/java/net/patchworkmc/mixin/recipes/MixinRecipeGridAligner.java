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

import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraftforge.common.crafting.IShapedRecipe;

import net.minecraft.util.math.MathHelper;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeGridAligner;

@Mixin(RecipeGridAligner.class)
public interface MixinRecipeGridAligner {
	@Shadow
	void acceptAlignedInput(Iterator<?> inputs, int slot, int amount, int gridX, int gridY);

	/**
	 * @author qouteall
	 * @reason https://github.com/FabricMC/Mixin/issues/15
	 */
	@Overwrite
	default void alignRecipeToGrid(
			int gridWidth, int gridHeight, int gridOutputSlot,
			Recipe<?> recipe, Iterator<?> inputs, int amount
	) {
		int width = gridWidth;
		int height = gridHeight;

		// change start
		if (recipe instanceof IShapedRecipe) {
			IShapedRecipe<?> shapedRecipe = (IShapedRecipe<?>) recipe;
			width = shapedRecipe.getRecipeWidth();
			height = shapedRecipe.getRecipeHeight();
		}

		// change end

		int slot = 0;

		for (int y = 0; y < gridHeight; ++y) {
			if (slot == gridOutputSlot) {
				++slot;
			}

			boolean bl = (float) height < (float) gridHeight / 2.0F;

			int m = MathHelper.floor((float) gridHeight / 2.0F - (float) height / 2.0F);

			if (bl && m > y) {
				slot += gridWidth;
				++y;
			}

			for (int x = 0; x < gridWidth; ++x) {
				if (!inputs.hasNext()) {
					return;
				}

				bl = (float) width < (float) gridWidth / 2.0F;
				m = MathHelper.floor((float) gridWidth / 2.0F - (float) width / 2.0F);
				int o = width;

				boolean bl2 = x < width;

				if (bl) {
					o = m + width;
					bl2 = m <= x && x < m + width;
				}

				if (bl2) {
					this.acceptAlignedInput(inputs, slot, amount, y, x);
				} else if (o == x) {
					slot += gridWidth - x;
					break;
				}

				++slot;
			}
		}
	}
}
