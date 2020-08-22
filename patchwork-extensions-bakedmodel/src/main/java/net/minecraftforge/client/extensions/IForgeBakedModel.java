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

package net.minecraftforge.client.extensions;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.client.model.data.IModelData;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

public interface IForgeBakedModel {
	default BakedModel getBakedModel() {
		return (BakedModel) this;
	}

	@Nonnull
	default List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
		return getBakedModel().getQuads(state, side, rand);
	}

	default boolean isAmbientOcclusion(BlockState state) {
		return getBakedModel().useAmbientOcclusion();
	}

	/*
	 * Returns the pair of the model for the given perspective, and the matrix that
	 * should be applied to the GL state before rendering it (matrix may be null).
	 */
	// default org.apache.commons.lang3.tuple.Pair<? extends BakedModel, javax.vecmath.Matrix4f> handlePerspective(ModelTransformation.Type cameraTransformType) {
	//	return net.minecraftforge.client.ForgeHooksClient.handlePerspective(getBakedModel(), cameraTransformType);
	// }

	@Nonnull
	default IModelData getModelData(@Nonnull BlockRenderView world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
		return tileData;
	}

	default Sprite getParticleTexture(@Nonnull IModelData data) {
		return getBakedModel().getSprite();
	}
}
