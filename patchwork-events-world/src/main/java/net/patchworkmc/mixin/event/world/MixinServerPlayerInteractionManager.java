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

package net.patchworkmc.mixin.event.world;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

import net.patchworkmc.impl.event.world.WorldEvents;
import net.patchworkmc.impl.extensions.block.BlockHarvestManager;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class MixinServerPlayerInteractionManager {
	@Shadow
	public ServerWorld world;
	@Shadow
	public ServerPlayerEntity player;
	@Shadow
	private GameMode gameMode;

	@Inject(method = "tryBreakBlock", at = @At("HEAD"), cancellable = true)
	private void hookBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> callback) {
		int exp = WorldEvents.onBlockBreakEvent(world, gameMode, player, pos);

		if (exp < 0) {
			callback.setReturnValue(false);
		} else {
			BlockHarvestManager.pushExpDropStack(exp);
		}
	}

	@Inject(method = "tryBreakBlock", at = @At("RETURN"), cancellable = true)
	private void tryBreakBlock_return(BlockPos pos, CallbackInfoReturnable<Boolean> callback) {
		BlockHarvestManager.popExpDropStack();
	}
}
