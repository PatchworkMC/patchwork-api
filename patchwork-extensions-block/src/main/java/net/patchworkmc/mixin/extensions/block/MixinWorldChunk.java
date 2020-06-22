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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import net.minecraftforge.common.extensions.IForgeBlockState;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

import net.patchworkmc.impl.extensions.block.BlockContext;
import net.patchworkmc.impl.extensions.block.Signatures;

@Mixin(WorldChunk.class)
public abstract class MixinWorldChunk {
	/**
	 * @param blockState
	 * @return the blockEntity created by IForgeBlock.createTileEntity(BlockState, World)
	 * , null if the block(blockstate) does not have a BlockEntity
	 */
	private BlockEntity patchwork_createBlockEntity(BlockState blockState) {
		BlockEntity blockEntity = null;

		if (BlockContext.hasBlockEntity(blockState)) {
			WorldChunk me = (WorldChunk) (Object) this;
			IForgeBlockState forgeBlockState = (IForgeBlockState) blockState;
			return forgeBlockState.createTileEntity(me.getWorld());
		}

		return blockEntity;
	}

	////////////////////////
	/// createBlockEntity()
	////////////////////////
	@Inject(method = "createBlockEntity", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true, at = @At(value = "INVOKE", target = Signatures.BlockState_getBlock))
	public void patchwork_createBlockEntity(BlockPos blockPos, CallbackInfoReturnable<BlockEntity> info, BlockState blockState) {
		info.setReturnValue(patchwork_createBlockEntity(blockState));
		info.cancel();
	}

	////////////////////////
	/// loadBlockEntity
	////////////////////////
	// Block block = this.getBlockState(pos).getBlock();
	// if (block instanceof BlockEntityProvider) {
	//     blockEntity3 = ((BlockEntityProvider)block).createBlockEntity(this.world);
	private static final ThreadLocal<Object> loadBlockEntity_blockEntity = BlockContext.createContext();
	@Redirect(method = "loadBlockEntity", at = @At(value = "INVOKE", target = Signatures.BlockState_getBlock, ordinal = 0))
	public Block patchwork_loadBlockEntity_getBlock(BlockState blockState) {
		BlockEntity blockEntity = patchwork_createBlockEntity(blockState);

		if (blockEntity != null) {
			BlockContext.pushContext(loadBlockEntity_blockEntity, blockEntity);
		}

		return BlockContext.hasBlockEntityBlockMarker(blockEntity != null);
	}

	@Redirect(method = "loadBlockEntity", at = @At(value = "INVOKE", target = Signatures.BlockEntityProvider_createBlockEntity, ordinal = 0))
	public BlockEntity patchwork_loadBlockEntity_createBlockEntity(BlockEntityProvider dummy, BlockView view) {
		BlockEntity blockEntity = BlockContext.popContext(loadBlockEntity_blockEntity);
		return blockEntity;
	}

	////////////////////////
	/// setBlockState()
	////////////////////////
	private static final ThreadLocal<Object> loadBlockEntity_blockState2 = BlockContext.createContext();
	// } else if (block2 != block && block2 instanceof BlockEntityProvider) {
	@Inject(method = "setBlockState", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "CONSTANT", args = Signatures.PATCHWORK_YARN_CLS_BLOCKENTITYPROVIDER, ordinal = 0), require = 0)
	public void patchwork_yarn_setBlockState_instanceof_BlockEntityProvider(BlockPos pos, BlockState state, boolean bl, CallbackInfoReturnable<BlockState> cir, int i, int j, int k, ChunkSection chunkSection, boolean bl2, BlockState blockState, Block block, Block block2) {
		BlockContext.pushContext(loadBlockEntity_blockState2, blockState);
	}

	@Inject(method = "setBlockState", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "CONSTANT", args = Signatures.PATCHWORK_REOBF_CLS_BLOCKENTITYPROVIDER, ordinal = 0), require = 0)
	public void patchwork_reobf_setBlockState_instanceof_BlockEntityProvider(BlockPos pos, BlockState state, boolean bl, CallbackInfoReturnable<BlockState> cir, int i, int j, int k, ChunkSection chunkSection, boolean bl2, BlockState blockState, Block block, Block block2) {
		BlockContext.pushContext(loadBlockEntity_blockState2, blockState);
	}

	@ModifyConstant(method = "setBlockState", constant = @Constant(classValue = BlockEntityProvider.class, ordinal = 0))
	public boolean patchwork_setBlockState_instanceof_BlockEntityProvider(Object object, Class<?> clazz) {
		BlockState blockState2 = BlockContext.popContext(loadBlockEntity_blockState2);
		return BlockContext.hasBlockEntity(blockState2);
	}

	// if (block2 instanceof BlockEntityProvider) {
	@Inject(method = "setBlockState", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "CONSTANT", args = Signatures.PATCHWORK_YARN_CLS_BLOCKENTITYPROVIDER, ordinal = 1), require = 0)
	public void patchwork_yarn_setBlockState_instanceof_BlockEntityProvider_1(BlockPos pos, BlockState state, boolean bl, CallbackInfoReturnable<BlockState> cir, int i, int j, int k, ChunkSection chunkSection, boolean bl2, BlockState blockState, Block block, Block block2) {
		BlockContext.pushContext(loadBlockEntity_blockState2, blockState);
	}

	@Inject(method = "setBlockState", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "CONSTANT", args = Signatures.PATCHWORK_REOBF_CLS_BLOCKENTITYPROVIDER, ordinal = 1), require = 0)
	public void patchwork_reobf_setBlockState_instanceof_BlockEntityProvider_1(BlockPos pos, BlockState state, boolean bl, CallbackInfoReturnable<BlockState> cir, int i, int j, int k, ChunkSection chunkSection, boolean bl2, BlockState blockState, Block block, Block block2) {
		BlockContext.pushContext(loadBlockEntity_blockState2, blockState);
	}

	@ModifyConstant(method = "setBlockState", constant = @Constant(classValue = BlockEntityProvider.class, ordinal = 1))
	public boolean patchwork_setBlockState_instanceof_BlockEntityProvider_1(Object object, Class<?> clazz) {
		BlockState blockState2 = BlockContext.popContext(loadBlockEntity_blockState2);
		return BlockContext.hasBlockEntity(blockState2);
	}

	// if (block instanceof BlockEntityProvider) {
	@ModifyConstant(method = "setBlockState", constant = @Constant(classValue = BlockEntityProvider.class, ordinal = 2))
	public boolean patchwork_setBlockState_instanceof_BlockEntityProvider_2(Object object, Class<?> clazz, BlockPos pos, BlockState state, boolean bl) {
		return BlockContext.hasBlockEntity(state);
	}

	@Redirect(method = "setBlockState", at = @At(value = "INVOKE", target = Signatures.BlockEntityProvider_createBlockEntity, ordinal = 0))
	public BlockEntity patchwork_setBlockState_createBlockEntity(BlockEntityProvider invoker, BlockView view, BlockPos pos, BlockState state, boolean bl) {
		IForgeBlockState forgeBlockState = (IForgeBlockState) state;
		return forgeBlockState.createTileEntity(view);
	}

	////////////////////////
	/// setBlockEntity()
	////////////////////////
	// if (this.getBlockState(pos).getBlock() instanceof BlockEntityProvider) {
	@Redirect(method = "setBlockEntity", at = @At(value = "INVOKE", target = Signatures.BlockState_getBlock, ordinal = 0))
	public Block patchwork_setBlockEntity_getBlock(BlockState blockState) {
		return BlockContext.hasBlockEntityBlockMarker(blockState);
	}
}
