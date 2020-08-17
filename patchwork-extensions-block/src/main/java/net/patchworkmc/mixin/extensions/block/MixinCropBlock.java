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

import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.extensions.IForgeBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

@Mixin(CropBlock.class)
public class MixinCropBlock {
	private static ThreadLocal<Block> currentBlock = new ThreadLocal<>();
	private static ThreadLocal<BlockView> currentWorld = new ThreadLocal<>();
	private static ThreadLocal<BlockPos> currentBlockPos = new ThreadLocal<>();

	@Inject(method = "getAvailableMoisture", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/BlockView;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void onGetBlockState(Block block, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir, float f, BlockPos blockPos, int i, int j) {
		currentBlock.set(block);
		currentWorld.set(world);
		currentBlockPos.set(blockPos.add(i, 0, j));
	}

	@Redirect(method = "getAvailableMoisture", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;", ordinal = 0))
	private static Block redirectFarmlandCheck(BlockState blockState) {
		final IForgeBlockState forgeBlockState = (IForgeBlockState) blockState;
		return forgeBlockState.canSustainPlant(currentWorld.get(), currentBlockPos.get(), Direction.UP, (IPlantable) currentBlock.get()) ? Blocks.FARMLAND : Blocks.STONE;
	}

	@SuppressWarnings("rawtypes")
	@Redirect(method = "getAvailableMoisture", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;get(Lnet/minecraft/state/property/Property;)Ljava/lang/Comparable;"))
	private static Comparable<Integer> redirectHydratedCheck(BlockState blockState, Property property) {
		final IForgeBlockState forgeBlockState = (IForgeBlockState) blockState;
		return forgeBlockState.isFertile(currentWorld.get(), currentBlockPos.get()) ? 1 : 0;
	}
}
