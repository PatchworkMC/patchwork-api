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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import net.patchworkmc.impl.extensions.block.BlockHarvestManager;

@Mixin(FarmlandBlock.class)
public class MixinFarmlandBlock extends Block {
	public MixinFarmlandBlock() {
		super(null);
	}

	@Inject(method = "hasCrop", cancellable = true, at = @At("HEAD"))
	private static void onHasCrop(BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		final BlockState ourState = world.getBlockState(pos);
		final BlockState cropState = world.getBlockState(pos.up());

		if (cropState.getBlock() instanceof IPlantable && ((IForgeBlockState) ourState).canSustainPlant(world, pos, Direction.UP, (IPlantable) cropState.getBlock())) {
			cir.setReturnValue(true);
			cir.cancel();
		}
	}

	@Inject(method = "onLandedUpon", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/FarmlandBlock;setToDirt(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"), cancellable = true)
	private void onSetToDirt(World world, BlockPos pos, Entity entity, float distance, CallbackInfo ci) {
		if (!BlockHarvestManager.onFarmlandTrample(world, pos, world.getBlockState(pos), distance, entity)) {
			super.onLandedUpon(world, pos, entity, distance);
			ci.cancel();
		}
	}
}
