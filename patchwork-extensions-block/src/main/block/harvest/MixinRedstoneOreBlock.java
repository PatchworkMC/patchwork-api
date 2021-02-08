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

package net.patchworkmc.mixin.extensions.block.harvest;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraftforge.common.extensions.IForgeBlock;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CollisionView;

@Mixin(RedstoneOreBlock.class)
public abstract class MixinRedstoneOreBlock implements IForgeBlock {
	@Override
	public int getExpDrop(BlockState state, CollisionView world, BlockPos pos, int fortune, int silktouch) {
		return silktouch == 0 ? 1 + getRandom().nextInt(5) : 0;
	}
}
