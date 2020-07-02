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

package net.patchworkmc.mixin.extensions.block.blockentity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraftforge.common.extensions.IForgeBlockState;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;

import net.patchworkmc.impl.extensions.block.BlockContext;
import net.patchworkmc.impl.extensions.block.Signatures;

@Mixin(ChunkRegion.class)
public abstract class MixinChunkRegion {
	////////////////////////
	/// getBlockEntity()
	////////////////////////
	private static final ThreadLocal<Object> getBlockEntity_blockState = BlockContext.createContext();
	// Block block = this.getBlockState(pos).getBlock();
	// if (!(block instanceof BlockEntityProvider)) {
	@Redirect(method = "getBlockEntity", at = @At(value = "INVOKE", target = Signatures.BlockState_getBlock, ordinal = 0))
	private Block patchwork_getBlockEntity_getBlock_0(BlockState blockState) {
		boolean hasBlockEntity = BlockContext.hasBlockEntity(blockState);

		if (hasBlockEntity) {
			BlockContext.setContext(getBlockEntity_blockState, blockState);
		}

		return BlockContext.hasBlockEntityBlockMarker(hasBlockEntity);
	}

	// blockEntity = ((BlockEntityProvider)block).createBlockEntity(this.world);
	@Redirect(method = "getBlockEntity", at = @At(value = "INVOKE", target = Signatures.BlockEntityProvider_createBlockEntity, ordinal = 0))
	private BlockEntity patchwork_getBlockEntity_createBlockEntity(BlockEntityProvider dummy, BlockView view) {
		IForgeBlockState forgeBlockState = BlockContext.releaseContext(getBlockEntity_blockState);
		return forgeBlockState.createTileEntity(view);
	}

	// if (chunk.getBlockState(pos).getBlock() instanceof BlockEntityProvider) {
	@Redirect(method = "getBlockEntity", at = @At(value = "INVOKE", target = Signatures.BlockState_getBlock, ordinal = 1))
	private Block patchwork_getBlockEntity_getBlock_1(BlockState blockState) {
		return BlockContext.hasBlockEntityBlockMarker(blockState);
	}

	////////////////////////
	/// setBlockState()
	////////////////////////
	// if (block.hasBlockEntity()) {
	@Redirect(method = "setBlockState", at = @At(value = "INVOKE", target = Signatures.Block_hasBlockEntity, ordinal = 0))
	private boolean patchwork_setBlockState_hasBlockEntity(Block block, BlockPos pos, BlockState state, int flags) {
		return BlockContext.hasBlockEntity(state);
	}

	// chunk.setBlockEntity(pos, ((BlockEntityProvider)block).createBlockEntity(this));
	@Redirect(method = "setBlockState", at = @At(value = "INVOKE", target = Signatures.BlockEntityProvider_createBlockEntity, ordinal = 0))
	private BlockEntity patchwork_setBlockState_createBlockEntity(BlockEntityProvider dummy, BlockView view, BlockPos pos, BlockState state, int flags) {
		return ((IForgeBlockState) state).createTileEntity(view);
	}

	// } else if (blockState != null && blockState.getBlock().hasBlockEntity()) {
	@Redirect(method = "setBlockState", at = @At(value = "INVOKE", target = Signatures.BlockState_getBlock, ordinal = 1))
	public Block patchwork_setBlockState_getBlock_1(BlockState blockstate) {
		return BlockContext.hasBlockEntityBlockMarker(blockstate);
	}

	///////////////////////
	/// breakBlock
	///////////////////////
	@Redirect(method = "breakBlock", at = @At(value = "INVOKE", target = Signatures.BlockState_getBlock, ordinal = 0))
	private Block patchwork_breakBlock_getBlock(BlockState blockstate) {
		return BlockContext.hasBlockEntityBlockMarker(blockstate);
	}
}
