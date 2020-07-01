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
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.chunk.ChunkRenderer;

import net.patchworkmc.impl.extensions.block.BlockContext;
import net.patchworkmc.impl.extensions.block.Signatures;

@Mixin(ChunkRenderer.class)
public abstract class MixinChunkRenderer {
	private static final ThreadLocal<Object> rebuildChunk_blockState = BlockContext.createContext();
	// Block block = blockState.getBlock();
	@Redirect(method = "rebuildChunk", at = @At(value = "INVOKE", target = Signatures.BlockState_getBlock, ordinal = 0))
	public Block patchwork_rebuildChunk_getBlock(BlockState blockstate) {
		BlockContext.setContext(rebuildChunk_blockState, blockstate);
		return blockstate.getBlock();
	}

	// if (block.hasBlockEntity()) {
	@Redirect(method = "rebuildChunk", at = @At(value = "INVOKE", target = Signatures.Block_hasBlockEntity, ordinal = 0))
	public boolean patchwork_rebuildChunk_hasBlockEntity(Block dummy) {
		BlockState blockState = BlockContext.releaseContext(rebuildChunk_blockState);
		return BlockContext.hasBlockEntity(blockState);
	}
}
