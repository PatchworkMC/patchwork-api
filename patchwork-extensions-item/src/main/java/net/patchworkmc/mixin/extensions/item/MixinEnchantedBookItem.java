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

package net.patchworkmc.mixin.extensions.item;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraftforge.common.extensions.IForgeItem;

import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Mixin(EnchantedBookItem.class)
public abstract class MixinEnchantedBookItem implements IForgeItem {
	@Override
	public String getCreatorModId(ItemStack itemStack) {
		final Item item = itemStack.getItem();
		final Identifier defaultId = Registry.ITEM.getDefaultId();
		final Identifier id = Registry.ITEM.getId(item);

		if (defaultId.equals(id) && item != Registry.ITEM.get(defaultId)) {
			return null;
		} else {
			final String namespace = id.getNamespace();

			if ("minecraft".equals(namespace)) {
				final ListTag enchantments = EnchantedBookItem.getEnchantmentTag(itemStack);

				if (enchantments.size() == 1) {
					final Identifier enchantmentId = Identifier.tryParse(enchantments.getCompoundTag(0).getString("id"));

					if (Registry.ENCHANTMENT.getOrEmpty(enchantmentId).isPresent()) {
						return enchantmentId.getNamespace();
					}
				}
			}

			return namespace;
		}
	}
}
