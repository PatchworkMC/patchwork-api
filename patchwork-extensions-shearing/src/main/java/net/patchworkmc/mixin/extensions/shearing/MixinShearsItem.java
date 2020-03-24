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

package net.patchworkmc.mixin.extensions.shearing;

import net.minecraftforge.common.IShearable;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import net.patchworkmc.impl.extensions.shearing.Shearables;

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
		if (entity.world.isClient) {
			return false;
		}

		// Avoid duplicating vanilla interactions
		if (this == Items.SHEARS) {
			EntityType<?> type = entity.getType();

			if (type == EntityType.MOOSHROOM || type == EntityType.SHEEP || type == EntityType.SNOW_GOLEM) {
				return false;
			}
		}

		if (entity instanceof IShearable) {
			IShearable target = (IShearable) entity;
			BlockPos pos = entity.getBlockPos();

			if (target.isShearable(stack, entity.world, pos)) {
				Shearables.shearEntity(stack, entity.world, pos, target);
			}

			return true;
		}

		return false;
	}
}
