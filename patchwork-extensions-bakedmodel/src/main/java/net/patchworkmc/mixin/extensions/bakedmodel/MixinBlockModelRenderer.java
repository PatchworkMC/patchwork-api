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

import java.util.List;
import java.util.Random;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraftforge.client.extensions.IForgeBakedModel;
import net.minecraftforge.client.model.data.IModelData;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import net.patchworkmc.impl.extensions.bakedmodel.ModelDataParameter;
import net.patchworkmc.impl.extensions.bakedmodel.ForgeBlockModelRenderer;

/**
 * Implements {@link net.minecraftforge.client.extensions.IForgeBakedModel#getQuads(BlockState,Direction,Random,IModelData)} and
 * {@link net.minecraftforge.client.extensions.IForgeBakedModel#shouldApplyDiffuseLighting()}.
 */
@Mixin(value = BlockModelRenderer.class, priority = 999)
public abstract class MixinBlockModelRenderer implements ForgeBlockModelRenderer {
	/////////////////////
	/// tesselate
	/////////////////////
	@Unique
	private static final ModelDataParameter tesselate_modelData = new ModelDataParameter();

	@Override
	public void patchwork$tesselate_ModelData(IModelData modelData) {
		tesselate_modelData.setFuncParam(modelData);
	}

	// This injection is not HEAD, so that it is compatible with Fabric API.
	@Inject(method = "tesselate", at = @At(value = "JUMP", opcode = Opcodes.IFEQ, ordinal = 0, shift = Shift.BEFORE))
	private void hookHead_tesselate(BlockRenderView view, BakedModel model, BlockState state, BlockPos pos, BufferBuilder buffer, boolean testSides, Random random, long l,
			CallbackInfoReturnable<Boolean> ci) {
		IModelData modelData = tesselate_modelData.getFuncParamAndReset();
		modelData = ((IForgeBakedModel) model).getModelData(view, pos, state, modelData);
		patchwork$tesselateSmoothFlat_ModelData(modelData);
	}

	@Inject(method = "tesselate", at = @At("RETURN"))
	private void hookReturn_tesselate(CallbackInfoReturnable<Boolean> ci) {
		tesselate_modelData.getFuncParamAndReset();
	}

	////////////////////////////////////////
	/// tesselateSmooth & tesselateFlat
	////////////////////////////////////////
	@Unique
	private static final ModelDataParameter tesselateSmoothFlat_modelData = new ModelDataParameter();

	@Override
	public void patchwork$tesselateSmoothFlat_ModelData(IModelData modelData) {
		tesselateSmoothFlat_modelData.setFuncParam(modelData);
	}

	@Inject(method = {"tesselateSmooth", "tesselateFlat"}, at = @At("HEAD"))
	private void hookHead_tesselateSmoothFlat(CallbackInfoReturnable<Boolean> ci) {
		tesselateSmoothFlat_modelData.setupLocalVarFromParam();
	}

	@Redirect(method = {"tesselateSmooth", "tesselateFlat"}, at = @At(value = "INVOKE", target =
			"Lnet/minecraft/client/render/model/BakedModel;getQuads(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;Ljava/util/Random;)Ljava/util/List;"))
	private List<BakedQuad> getQuads_tesselateSmoothFlat(BakedModel bakedModel, BlockState blockState, Direction side, Random random) {
		IModelData modelData = tesselateSmoothFlat_modelData.getLocalVar();
		return ((IForgeBakedModel) bakedModel).getQuads(blockState, side, random, modelData);
	}

	@Inject(method = {"tesselateSmooth", "tesselateFlat"}, at = @At("RETURN"))
	private void hookReturn_tesselateSmoothFlat(CallbackInfoReturnable<Boolean> ci) {
		tesselateSmoothFlat_modelData.releaseLocalVar();
	}

	/////////////////////////////////////////////
	/// tesselateFlat
	/// bakedquad.shouldApplyDiffuseLighting()
	/////////////////////////////////////////////
	// https://github.com/PatchworkMC/YarnForge/blob/04d384add800bc395f4934507721f72eb733389f/patches/minecraft/net/minecraft/client/render/block/BlockModelRenderer.java.patch
}
