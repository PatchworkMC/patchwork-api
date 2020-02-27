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

package com.patchworkmc.mixin.extension;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraftforge.common.extensions.IForgeItem;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Mixin(SpawnEggItem.class)
public abstract class MixinSpawnEggItem implements IForgeItem {
	@Override
	public String getCreatorModId(ItemStack itemStack) {
		final Item item = itemStack.getItem();
		Identifier defaultId = Registry.ITEM.getDefaultId();
		Identifier id = Registry.ITEM.getId(item);

		if (defaultId.equals(id) && item != Registry.ITEM.get(defaultId)) {
			return null;
		} else {
			final String namespace = id.getNamespace();

			if ("minecraft".equals(namespace)) {
				final EntityType<?> type = ((SpawnEggItem) item).getEntityType(itemStack.getTag());
				id = Registry.ENTITY_TYPE.getId(type);
				defaultId = Registry.ENTITY_TYPE.getDefaultId();

				if (defaultId.equals(id) && type != Registry.ENTITY_TYPE.get(defaultId)) {
					return namespace;
				}

				return id.getNamespace();
			}

			return namespace;
		}
	}
}