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

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraftforge.common.extensions.IForgeBlockState;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.patchworkmc.impl.extensions.block.Signatures;

// This is a 1.15+ patch, should not be used in 1.14.4
@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager {
	@Shadow
	@Final
	private MinecraftClient client;

	@Redirect(method = "breakBlock", at = @At(value = "INVOKE", target = Signatures.Block_onBreak, ordinal = 0))
	private void patchwork$breakBlock_onBreak(Block block, World world, BlockPos pos, BlockState state, PlayerEntity player) {
		// Suppress this call: block.onBreak(world, pos, blockState, this.client.player);
	}

	@Redirect(method = "breakBlock", at = @At(value = "INVOKE", target = Signatures.World_setBlockState, ordinal = 0))
	private boolean patchwork_breakBlock_setBlockState(World world, BlockPos pos, BlockState state, int flags) {
		FluidState ifluidstate = world.getFluidState(pos);
		return ((IForgeBlockState) state).removedByPlayer(world, pos, client.player, false, ifluidstate);
	}
}