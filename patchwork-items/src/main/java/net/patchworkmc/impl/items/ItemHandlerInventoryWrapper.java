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

package net.patchworkmc.impl.items;

import net.minecraftforge.items.IItemHandlerModifiable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class ItemHandlerInventoryWrapper implements Inventory {
	private IItemHandlerModifiable itemHandler;

	public ItemHandlerInventoryWrapper(IItemHandlerModifiable itemHandler) {
		this.itemHandler = itemHandler;
	}

	@Override
	public int size() {
		return itemHandler.getSlots();
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			if (!itemHandler.getStackInSlot(i).isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		return itemHandler.getStackInSlot(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return itemHandler.extractItem(slot, amount, false);
	}

	@Override
	public ItemStack removeStack(int slot) {
		ItemStack copy = itemHandler.getStackInSlot(slot).copy();
		itemHandler.setStackInSlot(slot, ItemStack.EMPTY);
		return copy;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		itemHandler.setStackInSlot(slot, stack);
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return false;
	}

	@Override
	public void clear() {
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			itemHandler.setStackInSlot(i, ItemStack.EMPTY);
		}
	}

	public IItemHandlerModifiable getItemHandler() {
		return this.itemHandler;
	}
}
