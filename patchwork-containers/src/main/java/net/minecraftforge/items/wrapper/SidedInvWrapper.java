/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.items.wrapper;

import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SidedInvWrapper implements IItemHandlerModifiable
{
	protected final SidedInventory inv;
	@Nullable
	protected final Direction side;

	@SuppressWarnings("unchecked")
	public static LazyOptional<IItemHandlerModifiable>[] create(SidedInventory inv, Direction... sides) {
		LazyOptional<IItemHandlerModifiable>[] ret = new LazyOptional[sides.length];
		for (int x = 0; x < sides.length; x++) {
			final Direction side = sides[x];
			ret[x] = LazyOptional.of(() -> new SidedInvWrapper(inv, side));
		}
		return ret;
	}

	public SidedInvWrapper(SidedInventory inv, @Nullable Direction side)
	{
		this.inv = inv;
		this.side = side;
	}

	public static int getSlot(SidedInventory inv, int slot, @Nullable Direction side)
	{
		int[] slots = inv.getInvAvailableSlots(side);
		if (slot < slots.length)
			return slots[slot];
		return -1;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		SidedInvWrapper that = (SidedInvWrapper) o;

		return inv.equals(that.inv) && side == that.side;
	}

	@Override
	public int hashCode()
	{
		int result = inv.hashCode();
		result = 31 * result + (side == null ? 0 : side.hashCode());
		return result;
	}

	@Override
	public int getSlots()
	{
		return inv.getInvAvailableSlots(side).length;
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot)
	{
		int i = getSlot(inv, slot, side);
		return i == -1 ? ItemStack.EMPTY : inv.getInvStack(i);
	}

	@Override
	@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
	{
		if (stack.isEmpty())
			return ItemStack.EMPTY;

		int slot1 = getSlot(inv, slot, side);

		if (slot1 == -1)
			return stack;

		ItemStack stackInSlot = inv.getInvStack(slot1);

		int m;
		if (!stackInSlot.isEmpty())
		{
			if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxCount(), getSlotLimit(slot)))
				return stack;

			if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot))
				return stack;

			if (!inv.canInsertInvStack(slot1, stack, side) || !inv.isValidInvStack(slot1, stack))
				return stack;

			m = Math.min(stack.getMaxCount(), getSlotLimit(slot)) - stackInSlot.getCount();

			if (stack.getCount() <= m)
			{
				if (!simulate)
				{
					ItemStack copy = stack.copy();
					copy.increment(stackInSlot.getCount());
					setInventorySlotContents(slot1, copy);
				}

				return ItemStack.EMPTY;
			}
			else
			{
				// copy the stack to not modify the original one
				stack = stack.copy();
				if (!simulate)
				{
					ItemStack copy = stack.split(m);
					copy.increment(stackInSlot.getCount());
					setInventorySlotContents(slot1, copy);
					return stack;
				}
				else
				{
					stack.decrement(m);
					return stack;
				}
			}
		}
		else
		{
			if (!inv.canInsertInvStack(slot1, stack, side) || !inv.isValidInvStack(slot1, stack))
				return stack;

			m = Math.min(stack.getMaxCount(), getSlotLimit(slot));
			if (m < stack.getCount())
			{
				// copy the stack to not modify the original one
				stack = stack.copy();
				if (!simulate)
				{
					setInventorySlotContents(slot1, stack.split(m));
					return stack;
				}
				else
				{
					stack.decrement(m);
					return stack;
				}
			}
			else
			{
				if (!simulate)
					setInventorySlotContents(slot1, stack);
				return ItemStack.EMPTY;
			}
		}

	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack)
	{
		int slot1 = getSlot(inv, slot, side);

		if (slot1 != -1)
			setInventorySlotContents(slot1, stack);
	}

	private void setInventorySlotContents(int slot, ItemStack stack) {
		inv.markDirty(); //Notify vanilla of updates, We change the handler to be responsible for this instead of the caller. So mimic vanilla behavior
		inv.setInvStack(slot, stack);
	}

	@Override
	@Nonnull
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		if (amount == 0)
			return ItemStack.EMPTY;

		int slot1 = getSlot(inv, slot, side);

		if (slot1 == -1)
			return ItemStack.EMPTY;

		ItemStack stackInSlot = inv.getInvStack(slot1);

		if (stackInSlot.isEmpty())
			return ItemStack.EMPTY;

		if (!inv.canExtractInvStack(slot1, stackInSlot, side))
			return ItemStack.EMPTY;

		if (simulate)
		{
			if (stackInSlot.getCount() < amount)
			{
				return stackInSlot.copy();
			}
			else
			{
				ItemStack copy = stackInSlot.copy();
				copy.setCount(amount);
				return copy;
			}
		}
		else
		{
			int m = Math.min(stackInSlot.getCount(), amount);
			ItemStack ret = inv.takeInvStack(slot1, m);
			inv.markDirty();
			return ret;
		}
	}

	@Override
	public int getSlotLimit(int slot)
	{
		return inv.getInvMaxStackAmount();
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack)
	{
		int slot1 = getSlot(inv, slot, side);
		return slot1 == -1 ? false : inv.isValidInvStack(slot1, stack);
	}
}
