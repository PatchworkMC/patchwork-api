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

package net.patchworkmc.api.registries;

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

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;

public abstract class PatchworkItemGroup extends ItemGroup {
	public PatchworkItemGroup(String name) {
		super(getNewArrayIndex(), name);
	}

	public PatchworkItemGroup(int index, String name) {
		this(name);

		if (index != -1 && index < ItemGroup.GROUPS.length - 1) {
			throw new IllegalArgumentException("ItemGroup constructor potentially tried to overwrite an existing creative tab!");
		}
	}

	private static int getNewArrayIndex() {
		// Get a new slot in the array
		FabricItemGroupBuilder.create(new Identifier("patchwork", "dummy")).build();

		return GROUPS.length - 1;
	}

	// for some reason tinyremapper fails to remap this method, so we just go around it
	@Override
	public ItemStack createIcon() {
		return patchwork$createIconHack();
	}

	public abstract ItemStack patchwork$createIconHack();
}
