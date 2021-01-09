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

package net.patchworkmc.impl.extensions.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.hit.HitResult;

public class PatchworkEntityItems {
	/**
	 * Get the {@link ItemStack} to be given to the player when they use the creative pick block button on an entity.
	 *
	 * <p>This provides the default implementation for {@link net.minecraftforge.common.extensions.IForgeEntity#getPickedResult(HitResult)},
	 * how forge mods will access this method.</p>
	 *
	 * @param entity The entity picked by the player.
	 * @return The ItemStack to be added to the player's inventory.
	 */
	public static ItemStack getPickedItem(Entity entity) {
		if (entity instanceof PaintingEntity) {
			return new ItemStack(Items.PAINTING);
		} else if (entity instanceof LeashKnotEntity) {
			return new ItemStack(Items.LEAD);
		} else if (entity instanceof ItemFrameEntity) {
			ItemStack held = ((ItemFrameEntity) entity).getHeldItemStack();

			if (held.isEmpty()) {
				return new ItemStack(Items.ITEM_FRAME);
			} else {
				return held.copy();
			}
		} else if (entity instanceof AbstractMinecartEntity) {
			// TODO: Once IForgeEntityMinecart is implemented, this should use IForgeEntityMinecart#getCartItem
			// (whose default implementation will be PatchworkEntityItems.getCartItem)
			return getCartItem((AbstractMinecartEntity) entity);
		} else if (entity instanceof BoatEntity) {
			return new ItemStack(((BoatEntity) entity).asItem());
		} else if (entity instanceof ArmorStandEntity) {
			return new ItemStack(Items.ARMOR_STAND);
		} else if (entity instanceof EndCrystalEntity) {
			return new ItemStack(Items.END_CRYSTAL);
		} else {
			SpawnEggItem egg = SpawnEggItem.forEntity(entity.getType());

			if (egg != null) {
				return new ItemStack(egg);
			}
		}

		return ItemStack.EMPTY;
	}

	/**
	 * Gets the {@link ItemStack} associated with a minecart entity. This is used to get the picked creative item, and
	 * is the default implementation for {@code IForgeEntityMinecart#getCartItem}.
	 *
	 * @param minecart The minecart entity.
	 * @return The ItemStack associated with this entity.
	 */
	public static ItemStack getCartItem(AbstractMinecartEntity minecart) {
		switch (minecart.getMinecartType()) {
			case FURNACE:
				return new ItemStack(Items.FURNACE_MINECART);
			case CHEST:
				return new ItemStack(Items.CHEST_MINECART);
			case TNT:
				return new ItemStack(Items.TNT_MINECART);
			case HOPPER:
				return new ItemStack(Items.HOPPER_MINECART);
			case COMMAND_BLOCK:
				return new ItemStack(Items.COMMAND_BLOCK_MINECART);
			default:
				return new ItemStack(Items.MINECART);
		}
	}
}
