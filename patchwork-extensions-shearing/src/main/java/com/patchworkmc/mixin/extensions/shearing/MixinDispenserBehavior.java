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
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.block.DispenserBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

/**
 * Patches the inner class containing the logic for dispensers when using shears, to allow them to shear any
 * IShearable entity.
 *
 * @author SuperCoder79
 */
@Mixin(targets = "net/minecraft/block/dispenser/DispenserBehavior$13")
public class MixinDispenserBehavior {
	@Unique
	protected boolean success;

	/**
	 * @reason Patch this class to drop stacks for any shearable entity. An overwrite was required here as the mixin was
	 * too complicated to write without one.
	 * @author SuperCoder79
	 */
	@Overwrite
	public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
		World world = pointer.getWorld();

		if (!world.isClient()) {
			this.success = false;
			BlockPos pos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
			List<Entity> entities = world.getEntities(Entity.class, new Box(pos));

			for (Entity entity : entities) {
				if (entity instanceof IShearable) {
					IShearable target = (IShearable) entity;

					if (target.isShearable(stack, world, pos)) {
						List<ItemStack> drops = target.onSheared(stack, world, pos, EnchantmentHelper.getLevel(Enchantments.FORTUNE, stack));
						Random rand = new Random();

						for (ItemStack drop : drops) {
							ItemEntity ent = entity.dropStack(drop, 1.0F);
							ent.setVelocity(ent.getVelocity().add((rand.nextFloat() - rand.nextFloat()) * 0.1F, rand.nextFloat() * 0.05F, (rand.nextFloat() - rand.nextFloat()) * 0.1F));
						}

						this.success = true;
					}
				}
			}
		}

		return stack;
	}
}
