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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;

@Mixin(PlantBlock.class)
public class MixinPlantBlock implements IPlantable {
	@Inject(method = "canPlaceAt", at = @At("HEAD"), cancellable = true)
	private void onCanPlaceAt(BlockState state, CollisionView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (state.getBlock() == (Object) this) {
			cir.setReturnValue(((IForgeBlockState) world.getBlockState(pos.down())).canSustainPlant(world, pos, Direction.UP, this));
			cir.cancel();
		}
	}

	@Override
	public BlockState getPlant(BlockView world, BlockPos pos) {
		final BlockState blockState = world.getBlockState(pos);

		if (blockState.getBlock() != (Object) this) {
			return ((Block) (Object) this).getDefaultState();
		}

		return blockState;
	}
}
