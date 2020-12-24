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

package net.patchworkmc.impl.extensions.bakedmodel;

import java.util.Random;

import net.minecraftforge.client.model.data.IModelData;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

/**
 * Forge has IModelData-sensitive version of functions. patchwork$xxx_ModelData(IModelData) sets the additional IModelData parameter.
 * Call patchwork$xxx_ModelData(IModelData) before invoking the vanilla function to mimic the behavior of the forge's function.
 */
public interface ForgeBlockRenderManager {
	/**
	 * Should be only be called just before invoking the vanilla BlockRenderManager::tesselateBlock function.
	 */
	void patchwork$tesselateBlock_ModelData(IModelData modelData);

	/**
	 * Forge's IModelData sensitive version. Supposed to be called by {{@link net.minecraft.client.render.chunk.ChunkRenderer#rebuildChunk}.
	 */
	default boolean renderBlock(BlockState blockState, BlockPos blockPos, BlockRenderView blockRenderView, BufferBuilder bufferBuilder, Random random, IModelData modelData) {
		BlockRenderManager me = (BlockRenderManager) this;
		patchwork$tesselateBlock_ModelData(modelData);
		return me.renderBlock(blockState, blockPos, blockRenderView, bufferBuilder, random);
	}
}
