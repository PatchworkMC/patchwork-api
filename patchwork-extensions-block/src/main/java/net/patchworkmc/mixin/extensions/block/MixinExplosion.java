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

import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.Explosion;

import net.patchworkmc.impl.extensions.block.BlockContext;
import net.patchworkmc.impl.extensions.block.Signatures;

@Mixin(Explosion.class)
public abstract class MixinExplosion {
	private static final ThreadLocal<Object> affectWorld_blockState = BlockContext.createContext();
	@Inject(method = "affectWorld", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = Signatures.Block_hasBlockEntity, ordinal = 0))
	private void patchwork_affectWorld_hasBlockEntity_before(boolean bl, CallbackInfo ci, boolean bl2, Iterator var3, BlockPos blockPos, BlockState blockState, Block block) {
		BlockContext.setContext(affectWorld_blockState, blockState);
	}

	@Redirect(method = "affectWorld", at = @At(value = "INVOKE", target = Signatures.Block_hasBlockEntity, ordinal = 0))
	public boolean patchwork_affectWorld_hasBlockEntity(Block dummy) {
		BlockState blockState = BlockContext.releaseContext(affectWorld_blockState);
		return BlockContext.hasBlockEntity(blockState);
	}
}
