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

package net.patchworkmc.mixin.extensions.block.flammable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraftforge.common.extensions.IForgeBlockState;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.patchworkmc.impl.extensions.block.Signatures;

@Mixin(World.class)
public abstract class MixinWorld {
	////////////////////////////////////////////////////////
	/// doesAreaContainFireSource - IForgeBlock.isBurning
	/// In 1.16.1, this patch is moved to Entity.
	////////////////////////////////////////////////////////
	// This really should be included in the Fabric API!
	// Block block = this.getBlockState(pooledMutable.set(o, p, q)).getBlock();
	// if (block == Blocks.FIRE || block == Blocks.LAVA) {
	@Redirect(method = "doesAreaContainFireSource", at = @At(value = "INVOKE", target = Signatures.World_getBlockState, ordinal = 0))
	private BlockState patchwork_doesAreaContainFireSource_getBlockState(World world, BlockPos blockPos) {
		BlockState blockState = world.getBlockState(blockPos);
		boolean isBurning = ((IForgeBlockState) blockState).isBurning(world, blockPos);
		return isBurning ? Blocks.FIRE.getDefaultState() : Blocks.WATER.getDefaultState();
	}
}
