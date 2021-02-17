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

package net.minecraftforge.items;

import org.jetbrains.annotations.NotNull;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public interface IItemHandler {
	/**
	 * Returns the number of slots available.
	 *
	 * @return The number of slots available
	 */
	int getSlots();

	/**
	 * Returns the ItemStack in a given slot.
	 *
	 * <p>The result's stack size may be greater than the itemstack's max size.
	 *
	 * <p>If the result is empty, then the slot is empty.
	 *
	 * <p><strong>IMPORTANT:</strong> This ItemStack <em>MUST NOT</em> be modified. This method is not for
	 * altering an inventory's contents. Any implementers who are able to detect
	 * modification through this method should throw an exception.
	 *
	 * <p><strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED ITEMSTACK</em></strong>
	 *
	 * @param slot Slot to query
	 * @return ItemStack in given slot. Empty Itemstack if the slot is empty.
	 */
	@NotNull
	ItemStack getStackInSlot(int slot);

	/**
	 * <p>Inserts an ItemStack into the given slot and return the remainder.
	 * The ItemStack <em>should not</em> be modified in this function!
	 * </p>
	 * Note: This behaviour is subtly different from net.minecraftforge.fluids.capability.IFluidHandler#fill(net.minecraftforge.fluids.FluidStack, boolean)
	 *
	 * @param slot     Slot to insert into.
	 * @param stack    ItemStack to insert. This must not be modified by the item handler.
	 * @param simulate If true, the insertion is only simulated
	 * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return an empty ItemStack).
	 * May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
	 * The returned ItemStack can be safely modified after.
	 */
	@NotNull
	ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate);

	/**
	 * Extracts an ItemStack from the given slot.
	 * <p>The returned value must be empty if nothing is extracted,
	 * otherwise its stack size must be less than or equal to {@code amount} and {@link ItemStack#getMaxCount()} ()}.
	 * </p>
	 *
	 * @param slot     Slot to extract from.
	 * @param amount   Amount to extract (may be greater than the current stack's max limit)
	 * @param simulate If true, the extraction is only simulated
	 * @return ItemStack extracted from the slot, must be empty if nothing can be extracted.
	 * The returned ItemStack can be safely modified after, so item handlers should return a new or copied stack.
	 */
	@NotNull
	ItemStack extractItem(int slot, int amount, boolean simulate);

	/**
	 * Retrieves the maximum stack size allowed to exist in the given slot.
	 *
	 * @param slot Slot to query.
	 * @return The maximum stack size allowed in the slot.
	 */
	int getSlotLimit(int slot);

	/**
	 * <p>This function re-implements the vanilla function {@link Inventory#isValid(int, ItemStack)}.
	 * It should be used instead of simulated insertions in cases where the contents and state of the inventory are
	 * irrelevant, mainly for the purpose of automation and logic (for instance, testing if a minecart can wait
	 * to deposit its items into a full inventory, or if the items in the minecart can never be placed into the
	 * inventory and should move on).
	 * </p>
	 * <ul>
	 * <li>isItemValid is false when insertion of the item is never valid.</li>
	 * <li>When isItemValid is true, no assumptions can be made and insertion must be simulated case-by-case.</li>
	 * <li>The actual items in the inventory, its fullness, or any other state are <strong>not</strong> considered by isItemValid.</li>
	 * </ul>
	 *
	 * @param slot  Slot to query for validity
	 * @param stack Stack to test with for validity
	 * @return true if the slot can insert the ItemStack, not considering the current state of the inventory.
	 * false if the slot can never insert the ItemStack in any situation.
	 */
	boolean isItemValid(int slot, @NotNull ItemStack stack);
}
