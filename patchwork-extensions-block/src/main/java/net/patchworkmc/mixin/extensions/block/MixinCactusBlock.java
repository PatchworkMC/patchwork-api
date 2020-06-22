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
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.extensions.IForgeBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CactusBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;

@Mixin(CactusBlock.class)
public class MixinCactusBlock implements IPlantable {
	private ThreadLocal<BlockPos> currentBlockPos = new ThreadLocal<>();
	private ThreadLocal<CollisionView> currentWorld = new ThreadLocal<>();

	@Inject(method = "canPlaceAt", at = @At("HEAD"))
	private void onCanPlaceAt(BlockState state, CollisionView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		currentBlockPos.set(pos);
		currentWorld.set(world);
	}

	@Redirect(method = "canPlaceAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"))
	private Block redirectSoilCheck(BlockState blockState) {
		if (((IForgeBlockState) blockState).canSustainPlant(currentWorld.get(), currentBlockPos.get().down(), Direction.UP, this)) {
			return Blocks.CACTUS; // Forces condition to be true.
		}

		return blockState.getBlock(); // Pass this to let injects from fabric mods maybe work
	}

	@Override
	public PlantType getPlantType(BlockView world, BlockPos pos) {
		return PlantType.Desert;
	}

	@Override
	public BlockState getPlant(BlockView world, BlockPos pos) {
		return ((Block) (Object) this).getDefaultState();
	}
}
