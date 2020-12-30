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

public class PatchworkEntityItems {
	public static ItemStack getEntityItem(Entity entity) {
		if (entity instanceof PaintingEntity) {
			return new ItemStack(Items.PAINTING);
		} else if (entity instanceof LeashKnotEntity) {
			return new ItemStack(Items.LEAD);
		} else if (entity instanceof ItemFrameEntity) {
			ItemStack held = ((ItemFrameEntity)entity).getHeldItemStack();
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
