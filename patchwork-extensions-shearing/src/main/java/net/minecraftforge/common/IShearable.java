package net.minecraftforge.common;

import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

import java.util.List;

/**
 *
 * This allows for mods to create there own Shear-like items
 * and have them interact with Blocks/Entities without extra work.
 * Also, if your block/entity supports the Shears, this allows you
 * to support mod-shears as well.
 *
 */
//TODO Change to World, not IWorldReader and make Implementor responsible for removing itself from the world.
//Better mimics vanilla behavior and allows more control for the user.

@Deprecated //TODO: Reevaluate the entire thing, loot from blocks is now a loot table. So this is just a marker now.
public interface IShearable {
	/**
	 * Checks if the object is currently shearable
	 * Example: Sheep return false when they have no wool
	 *
	 * @param item  The ItemStack that is being used, may be empty.
	 * @param world The current world.
	 * @param pos   Block's position in world.
	 * @return If this is shearable, and onSheared should be called.
	 */
	default boolean isShearable(ItemStack item, ViewableWorld world, BlockPos pos) {
		return true;
	}

	/**
	 * Performs the shear function on this object.
	 * This is called for both client, and server.
	 * The object should perform all actions related to being sheared,
	 * except for dropping of the items, and removal of the block.
	 * As those are handled by ItemShears itself.
	 * <p>
	 * Returns a list of items that resulted from the shearing process.
	 * <p>
	 * For entities, they should trust there internal location information
	 * over the values passed into this function.
	 *
	 * @param item    The ItemStack that is being used, may be empty.
	 * @param world   The current world.
	 * @param pos     If this is a block, the block's position in world.
	 * @param fortune The fortune level of the shears being used.
	 * @return A List containing all items from this shearing. May be empty.
	 */
	default List<ItemStack> onSheared(ItemStack item, IWorld world, BlockPos pos, int fortune) {
		return DefaultedList.of();
	}
}
