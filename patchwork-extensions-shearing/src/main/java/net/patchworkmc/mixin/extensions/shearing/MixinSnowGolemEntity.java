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

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.IShearable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CollisionView;
import net.minecraft.world.WorldAccess;

/**
 * Patches {@link SnowGolemEntity} to allow using {@link IShearable} for removing its pumpkin. The pumpkin will not be dropped as an item.
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
	public boolean isShearable(ItemStack item, CollisionView world, BlockPos pos) {
		return this.hasPumpkin();
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, WorldAccess world, BlockPos pos, int fortune) {
		this.setHasPumpkin(false);

		return new ArrayList<>();
	}
}
