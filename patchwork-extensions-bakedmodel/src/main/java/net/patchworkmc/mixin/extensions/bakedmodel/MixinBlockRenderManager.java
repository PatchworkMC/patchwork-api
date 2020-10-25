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

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraftforge.client.model.data.IModelData;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

import net.patchworkmc.impl.extensions.bakedmodel.ForgeBlockRenderManager;
import net.patchworkmc.impl.extensions.bakedmodel.ModelDataParameter;
import net.patchworkmc.impl.extensions.bakedmodel.ForgeBlockModelRenderer;

/**
 * tesselateBlock() and renderBlock().
 */
@Mixin(BlockRenderManager.class)
public abstract class MixinBlockRenderManager implements ForgeBlockRenderManager {
	@Unique
	private static final ModelDataParameter tesselateBlock_IModelData = new ModelDataParameter();

	@Override
	public void patchwork$tesselateBlock_ModelData(IModelData modelData) {
		tesselateBlock_IModelData.setFuncParam(modelData);
	}

	@Inject(method = "tesselateBlock", at = @At("HEAD"))
	private void hookHead_tesselateBlock(BlockState blockState, BlockPos blockPos, BlockRenderView blockRenderView, BufferBuilder bufferBuilder, Random random,
			CallbackInfoReturnable<Boolean> cir) {
		tesselateBlock_IModelData.setupLocalVarFromParam();
	}

	@Inject(method = "tesselateBlock", at = @At(value = "INVOKE", ordinal = 0, shift = Shift.BEFORE,
			target = "net/minecraft/client/render/block/BlockModelRenderer.tesselate("
					+ "Lnet/minecraft/world/BlockRenderView;"
					+ "Lnet/minecraft/client/render/model/BakedModel;"
					+ "Lnet/minecraft/block/BlockState;"
					+ "Lnet/minecraft/util/math/BlockPos;"
					+ "Lnet/minecraft/client/render/BufferBuilder;"
					+ "Z"
					+ "Ljava/util/Random;"
					+ "J)Z"))
	private void beforeBlockTesselate(CallbackInfoReturnable<Boolean> cir) {
		IModelData modelData = tesselateBlock_IModelData.getLocalVar();
		BlockRenderManager me = (BlockRenderManager) (Object) this;
		BlockModelRenderer render = me.getModelRenderer();
		((ForgeBlockModelRenderer) render).patchwork$tesselate_ModelData(modelData);
	}

	@Inject(method = "tesselateBlock", at = @At("RETURN"))
	private void hookReturn_tesselateBlock(CallbackInfoReturnable<Boolean> cir) {
		tesselateBlock_IModelData.releaseLocalVar();
	}
}
