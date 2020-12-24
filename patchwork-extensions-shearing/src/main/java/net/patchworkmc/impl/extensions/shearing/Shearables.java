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

package net.patchworkmc.impl.extensions.shearing;

import java.util.List;
import java.util.Random;

import net.minecraftforge.common.IShearable;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public class Shearables {
	public static void shearEntity(ItemStack stack, WorldAccess world, BlockPos pos, IShearable target) {
		if (!(target instanceof Entity)) {
			throw new IllegalArgumentException("Tried to call shearEntity on something that was not an entity!");
		}

		Entity entity = (Entity) target;

		List<ItemStack> drops = target.onSheared(stack, world, pos, EnchantmentHelper.getLevel(Enchantments.FORTUNE, stack));
		Random rand = world.getRandom();

		for (ItemStack drop : drops) {
			ItemEntity item = entity.dropStack(drop, 1.0F);

			if (item == null) {
				continue;
			}

			float accelerationX = (rand.nextFloat() - rand.nextFloat()) * 0.1F;
			float accelerationY = rand.nextFloat() * 0.05F;
			float accelerationZ = (rand.nextFloat() - rand.nextFloat()) * 0.1F;

			item.setVelocity(item.getVelocity().add(accelerationX, accelerationY, accelerationZ));
		}

		if (stack.damage(1, world.getRandom(), null)) {
			stack.setCount(0);
		}
	}
}
