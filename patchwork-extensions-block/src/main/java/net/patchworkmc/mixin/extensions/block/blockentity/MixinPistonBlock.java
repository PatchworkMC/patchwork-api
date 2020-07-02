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

package net.patchworkmc.mixin.extensions.block.blockentity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.patchworkmc.impl.extensions.block.BlockContext;
import net.patchworkmc.impl.extensions.block.Signatures;

@Mixin(PistonBlock.class)
public abstract class MixinPistonBlock {
	// public static boolean isMovable(BlockState state, World world, BlockPos pos, Direction motionDir, boolean canBreak, Direction pistonDir) {
	// return !block.hasBlockEntity();
	@Redirect(method = "isMovable", at = @At(value = "INVOKE", target = Signatures.Block_hasBlockEntity, ordinal = 0))
	private static boolean patchwork_isMovable_getBlock(Block dummy, BlockState state, World world, BlockPos pos, Direction motionDir, boolean canBreak, Direction pistonDir) {
		return BlockContext.hasBlockEntity(state);
	}

	// BlockEntity blockEntity = blockState4.getBlock().hasBlockEntity() ? world.getBlockEntity(blockPos5) : null;
	@Redirect(method = "move", at = @At(value = "INVOKE", target = Signatures.BlockState_getBlock, ordinal = 1))
	private Block patchwork_move_getBlock(BlockState blockstate) {
		return BlockContext.hasBlockEntityBlockMarker(blockstate);
	}
}
