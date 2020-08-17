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

package net.patchworkmc.mixin.extensions.block;

import java.util.Random;

import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.extensions.IForgeBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@Mixin(StemBlock.class)
public class MixinStemBlock {
	private ThreadLocal<World> currentWorld = new ThreadLocal<>();
	private ThreadLocal<BlockPos> currentBlockPos = new ThreadLocal<>();

	@Inject(method = "onScheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	private void onScheduledTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci, Direction direction, BlockPos blockPos) {
		currentWorld.set(world);
		currentBlockPos.set(blockPos);
	}

	@Redirect(method = "onScheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"))
	private Block redirectSoilCheck(BlockState blockState) {
		if (((IForgeBlockState) blockState).canSustainPlant(currentWorld.get(), currentBlockPos.get().down(), Direction.UP, (IPlantable) this)) {
			return Blocks.DIRT;
		}

		return blockState.getBlock();
	}
}
