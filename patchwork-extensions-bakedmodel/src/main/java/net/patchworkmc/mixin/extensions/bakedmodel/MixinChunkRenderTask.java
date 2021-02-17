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

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.chunk.ChunkRenderTask;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import net.patchworkmc.impl.extensions.bakedmodel.ForgeChunkRenderTask;

/**
 * Adds IModelData getter to vanilla ChunkRenderTask.
 */
@Mixin(ChunkRenderTask.class)
public abstract class MixinChunkRenderTask implements ForgeChunkRenderTask {
	private Map<BlockPos, IModelData> modelData;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init_return(ChunkRenderer chunkRenderer, ChunkRenderTask.Mode mode, double squaredCameraDistance, @Nullable ChunkRendererRegion region, CallbackInfo ci) {
		modelData = ModelDataManager.getModelData(MinecraftClient.getInstance().world,
				new ChunkPos(chunkRenderer.getOrigin()));
	}

	@Override
	public IModelData getModelData(BlockPos pos) {
		return modelData.getOrDefault(pos, EmptyModelData.INSTANCE);
	}
}
