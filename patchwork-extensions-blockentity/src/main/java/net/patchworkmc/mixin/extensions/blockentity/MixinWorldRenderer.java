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

package net.patchworkmc.mixin.extensions.blockentity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraftforge.common.extensions.IForgeTileEntity;

import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

/**
 * Implements {@link IForgeTileEntity#getRenderBoundingBox()} and
 * {@link IForgeTileEntity#canRenderBreaking()}.
 */
@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
	/////////////////////////////////////
	/// renderEntities()
	/////////////////////////////////////
	// Redirect two calls.
	@Redirect(method = "renderEntities", at = @At(value = "INVOKE",
			target = "net/minecraft/client/render/block/entity/BlockEntityRenderDispatcher.render(Lnet/minecraft/block/entity/BlockEntity;FI)V"))
	private void renderBlockEntityIfVisible(BlockEntityRenderDispatcher dispatcher, BlockEntity blockEntity, float tickDelta, int blockBreakStage,
			Camera camera, VisibleRegion visibleRegion, float tickDeltaParam) {
		IForgeTileEntity te = (IForgeTileEntity) blockEntity;

		if (visibleRegion.intersects(te.getRenderBoundingBox())) {
			dispatcher.render(blockEntity, tickDeltaParam, blockBreakStage);
		}
	}

	/////////////////////////////////////
	/// renderPartiallyBrokenBlocks()
	/////////////////////////////////////
	@Shadow
	private ClientWorld world;
	@Unique
	private static final ThreadLocal<BlockPos> blockPosParam = ThreadLocal.withInitial(() -> null);

	@Redirect(method = "renderPartiallyBrokenBlocks", at = @At(value = "INVOKE", ordinal = 0,
			target = "net/minecraft/client/render/BlockBreakingInfo.getPos()Lnet/minecraft/util/math/BlockPos;"))
	private BlockPos captureBlockPos(BlockBreakingInfo bbi) {
		if (blockPosParam.get() != null) {
			throw new IllegalStateException("State of WorldRenderer.renderPartiallyBrokenBlocks() is not clean, incompatible Mixins might be the cause!");
		}

		BlockPos pos = bbi.getPos();
		blockPosParam.set(pos);
		return pos;
	}

	@ModifyConstant(method = "renderPartiallyBrokenBlocks", constant = @Constant(classValue = ChestBlock.class))
	private boolean skipNormalPartialDamageRendering(Object objIn, Class<?> clsChestBlock) {
		BlockPos pos = blockPosParam.get();
		blockPosParam.remove();

		if (clsChestBlock.isAssignableFrom(objIn.getClass())) {
			return true; // objIn instanceof clsChestBlock
		}

		BlockEntity blockEntity = world.getBlockEntity(pos);

		if (blockEntity == null) {
			return false; // Render partial damage for normal blocks
		}

		return ((IForgeTileEntity) blockEntity).canRenderBreaking();
	}
}
