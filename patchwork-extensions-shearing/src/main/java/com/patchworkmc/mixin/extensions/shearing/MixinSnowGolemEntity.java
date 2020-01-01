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

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.IShearable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

/**
 * Patches {@link SnowGolemEntity} to use {@link IShearable} for removing its pumpkin. The pumpkin will not be dropped as an item. This patch cancels the vanilla shearing code.
 *
 * @author SuperCoder79
 */
@Mixin(SnowGolemEntity.class)
public abstract class MixinSnowGolemEntity implements IShearable {
	@Shadow
	public abstract boolean hasPumpkin();

	@Shadow
	public abstract void setHasPumpkin(boolean hasPumpkin);

	@Override
	public boolean isShearable(ItemStack item, ViewableWorld world, BlockPos pos) {
		return this.hasPumpkin();
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IWorld world, BlockPos pos, int fortune) {
		this.setHasPumpkin(false);

		return new ArrayList<>();
	}

	/**
	 * @reason The original patch only required a cancellation at the HEAD, but @Overwrite was chosen to make
	 * mod incompatibility easier to find.
	 * @author SuperCoder79
	 */
	@Overwrite
	public boolean interactMob(PlayerEntity player, Hand hand) {
		return false;
	}
}
