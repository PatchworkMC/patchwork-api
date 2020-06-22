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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;

import net.patchworkmc.impl.extensions.block.BlockContext;
import net.patchworkmc.impl.extensions.block.Signatures;

@Mixin(FallingBlockEntity.class)
public abstract class MixinFallingBlockEntity {
	////////////////////////
	/// tick()
	////////////////////////
	private static final ThreadLocal<Object> tick_blockState = BlockContext.createContext();
	// } else if (block2 != block && block2 instanceof BlockEntityProvider) {
	@Inject(method = "tick", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "CONSTANT", args = Signatures.PATCHWORK_YARN_CLS_BLOCKENTITYPROVIDER, ordinal = 0, shift = Shift.BEFORE))
	public void patchwork_yarn_tick_instanceof_BlockEntityProvider(CallbackInfo ci, Block block, BlockPos blockPos2) {
		FallingBlockEntity me = (FallingBlockEntity) (Object) this;
		BlockState blockState = me.world.getBlockState(blockPos2);
		BlockContext.pushContext(tick_blockState, blockState);
	}

	@ModifyConstant(method = "tick", constant = @Constant(classValue = BlockEntityProvider.class, ordinal = 0))
	public boolean patchwork_tick_instanceof_BlockEntityProvider(Object object, Class<?> clazz) {
		BlockState blockState2 = BlockContext.popContext(tick_blockState);
		return BlockContext.hasBlockEntity(blockState2);
	}
}
