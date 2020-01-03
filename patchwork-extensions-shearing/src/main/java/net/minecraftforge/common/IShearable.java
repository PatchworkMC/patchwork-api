/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2019, 2019
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

package net.minecraftforge.common;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

/**
 * This allows for mods to create their own Shear-like items
 * and have them interact with entities without extra work.
 * Also, if your block/entity supports shears, this allows you
 * to support mod-shears as well.
 */
@Deprecated
public interface IShearable {
	/**
	 * Checks if the object is currently shearable.
	 * Example: Sheep return false when they have no wool
	 *
	 * @param item  The {@link ItemStack} that is being used, may be empty.
	 * @param world The current world.
	 * @param pos   The current position in the world of the target block or entity.
	 * @return If this block/entity is shearable, and if {@link #onSheared} should be called.
	 */
	default boolean isShearable(ItemStack item, ViewableWorld world, BlockPos pos) {
		return true;
	}

	/**
	 * Performs the shear function on this object.
	 * This is called on both the client and the server.
	 * The object should perform all actions related to being sheared,
	 * except for dropping of the items, and removal of the block.
	 * Those functions are handled by {@link net.minecraft.item.ShearsItem} itself.
	 *
	 * <p>Returns a list of items to be dropped as a result of the shearing process.</p>
	 *
	 * <p>For entities, they should trust their internal location information
	 * over the values passed into this function.</p>
	 *
	 * @param item    The {@link ItemStack} that is being used, may be empty.
	 * @param world   The current world.
	 * @param pos     If this is a block, the block's position in world.
	 * @param fortune The fortune level of the shears being used.
	 * @return A List containing all items from this shearing. May be empty.
	 */
	default List<ItemStack> onSheared(ItemStack item, IWorld world, BlockPos pos, int fortune) {
		return DefaultedList.of();
	}
}
