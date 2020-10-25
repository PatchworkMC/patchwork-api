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

package net.patchworkmc.mixin.extensions.bakedmodel;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import net.minecraftforge.client.model.data.IModelData;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.client.render.chunk.ChunkRenderData;
import net.minecraft.client.render.chunk.ChunkRenderTask;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.impl.client.indigo.Indigo;

import net.patchworkmc.impl.extensions.bakedmodel.ForgeBlockRenderManager;
import net.patchworkmc.impl.extensions.bakedmodel.ForgeChunkRenderTask;

/**
 * Mimic calling Forge's IModelData sensitive version of tesselateBlock().
 */
@Mixin(ChunkRenderer.class)
public abstract class MixinChunkRenderer {
	/**
	 * This method can be better if we can talk to the Fabric API developer.
	 * If the implementation of {@link net.fabricmc.fabric.mixin.client.indigo.renderer.MixinChunkRenderer#hookChunkBuildTesselate}
	 * is placed in a separate class, it will be much easier for us.
	 */
	@Inject(method = "rebuildChunk", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 1, shift = Shift.BEFORE,
			target = "Lnet/minecraft/client/render/chunk/ChunkRenderData;isBufferInitialized(Lnet/minecraft/client/render/RenderLayer;)Z"))
	private void setModelData(float cameraX, float cameraY, float cameraZ, ChunkRenderTask task, CallbackInfo ci,
			ChunkRenderData chunkRenderData, BlockPos blockPos, BlockPos blockPos2, ChunkOcclusionDataBuilder chunkOcclusionDataBuilder, Set set, ChunkRendererRegion chunkRendererRegion, boolean[] bls, Random random, BlockRenderManager blockRenderManager, Iterator var16, BlockPos blockPos3, BlockState blockState, Block block, RenderLayer renderLayer2, int k, BufferBuilder bufferBuilder2) {
		if (blockState.getRenderType() == BlockRenderType.MODEL) {
			BakedModel model = blockRenderManager.getModel(blockState);

			if (Indigo.ALWAYS_TESSELATE_INDIGO || !((FabricBakedModel) model).isVanillaAdapter()) {
				return;
			}
		}

		IModelData modelData = ((ForgeChunkRenderTask) task).getModelData(blockPos3);
		((ForgeBlockRenderManager) blockRenderManager).patchwork$tesselateBlock_ModelData(modelData);
	}
}
