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
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

/**
 * Forge has IModelData-sensitive version of functions. patchwork$xxx_ModelData(IModelData) sets the additional IModelData parameter.
 * Call patchwork$xxx_ModelData(IModelData) before invoking the vanilla function to mimic the behavior of the forge's function.
 */
public interface ForgeBlockModelRenderer {
	/**
	 * Should be only be called just before invoking the vanilla BlockModelRenderer::tesselate function.
	 */
	void patchwork$tesselate_ModelData(IModelData modelData);
	/**
	 * Should be only be called just before invoking the vanilla BlockModelRenderer::tesselateSmooth or
	 * the BlockModelRenderer::tesselateFlat function.
	 */
	void patchwork$tesselateSmoothFlat_ModelData(IModelData modelData);

	/**
	 * Forge's modelData sensitive version.
	 */
	default boolean tesselate(BlockRenderView view, BakedModel model, BlockState state, BlockPos pos, BufferBuilder buffer, boolean testSides, Random random, long l, IModelData modelData) {
		BlockModelRenderer me = (BlockModelRenderer) this;
		patchwork$tesselate_ModelData(modelData);
		return me.render(view, model, state, pos, buffer, testSides, random, l);
	}

	default boolean renderModelSmooth(BlockRenderView view, BakedModel model, BlockState state, BlockPos pos, BufferBuilder buffer, boolean testSides, Random random, long l, IModelData modelData) {
		BlockModelRenderer me = (BlockModelRenderer) this;
		patchwork$tesselateSmoothFlat_ModelData(modelData);
		return me.renderSmooth(view, model, state, pos, buffer, testSides, random, l);
	}

	default boolean renderModelFlat(BlockRenderView view, BakedModel model, BlockState state, BlockPos pos, BufferBuilder buffer, boolean testSides, Random random, long l, IModelData modelData) {
		BlockModelRenderer me = (BlockModelRenderer) this;
		patchwork$tesselateSmoothFlat_ModelData(modelData);
		return me.renderFlat(view, model, state, pos, buffer, testSides, random, l);
	}
}
