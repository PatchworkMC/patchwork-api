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

import net.minecraftforge.common.extensions.IForgeBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.chunk.Chunk;

/**
 * See the comment in BaseToContextMapper in patchwork-extensions
 */
@Mixin(ChunkRegion.class)
public class MixinChunkRegion {
	@Unique
	private final ThreadLocal<BlockState> blockContext = ThreadLocal.withInitial(() -> null);

	@Inject(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockEntityProvider;createBlockEntity(Lnet/minecraft/world/BlockView;)Lnet/minecraft/block/entity/BlockEntity;",
			ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	private void captureBE(BlockPos pos, BlockState state, int arg2, int arg3, CallbackInfoReturnable<Boolean> cir, Chunk chunk, BlockState blockState) {
		blockContext.set(state);
	}

	@Redirect(method = "Lnet/minecraft/world/ChunkRegion;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockEntityProvider;createBlockEntity(Lnet/minecraft/world/BlockView;)Lnet/minecraft/block/entity/BlockEntity;", ordinal = 0))
	private BlockEntity redirectCreateBlockEntity(BlockEntityProvider blockEntityProvider, BlockView world) {
		BlockEntity ret = ((IForgeBlockState) blockContext.get()).createTileEntity(world);
		blockContext.remove();

		return ret;
	}
}
