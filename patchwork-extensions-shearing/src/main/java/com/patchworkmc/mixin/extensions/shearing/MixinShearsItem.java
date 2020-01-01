/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2019, 2019
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

package com.patchworkmc.mixin.extensions.shearing;

import java.util.List;
import java.util.Random;

import net.minecraftforge.common.IShearable;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

/**
 * Patch {@link ShearsItem} to allow it to shear any {@link IShearable}.
 *
 * @author SuperCoder79
 */
@Mixin(ShearsItem.class)
public class MixinShearsItem extends Item {
	public MixinShearsItem(Settings settings) {
		super(settings);
	}

	@Override
	public boolean useOnEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity entity, Hand hand) {
		if (entity.world.isClient) return false;

		if (entity instanceof IShearable) {
			IShearable target = (IShearable) entity;
			BlockPos pos = entity.getBlockPos();

			if (target.isShearable(stack, entity.world, pos)) {
				List<ItemStack> drops = target.onSheared(stack, entity.world, pos, EnchantmentHelper.getLevel(Enchantments.FORTUNE, stack));
				Random rand = new Random();

				for (ItemStack drop : drops) {
					ItemEntity ent = entity.dropStack(drop, 1.0F);
					ent.setVelocity(ent.getVelocity().add((rand.nextFloat() - rand.nextFloat()) * 0.1F, rand.nextFloat() * 0.05F, (rand.nextFloat() - rand.nextFloat()) * 0.1F));
				}

				stack.damage(1, entity, e -> e.sendToolBreakStatus(hand));
			}

			return true;
		}

		return false;
	}
}
