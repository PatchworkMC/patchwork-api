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

package net.patchworkmc.api.capability;

import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import net.patchworkmc.impl.capability.ItemStackCapabilityAccess;

/**
 * Subclassing ItemStack isn't a great idea, so we're going the factory route instead.
 */
public class PatchworkItemStack {
	public static ItemStack of(ItemConvertible in, int count, @Nullable CompoundTag capNBT) {
		ItemStack out = new ItemStack(in, count);
		ItemStackCapabilityAccess access = (ItemStackCapabilityAccess) (Object) out;
		access.patchwork$setCapNBT(capNBT);
		access.patchwork$initCaps();

		return out;
	}
}
