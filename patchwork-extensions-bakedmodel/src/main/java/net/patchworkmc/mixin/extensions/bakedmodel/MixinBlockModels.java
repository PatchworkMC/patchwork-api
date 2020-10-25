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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraftforge.client.extensions.IForgeBakedModel;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.patchworkmc.impl.extensions.bakedmodel.ForgeBlockModels;

/**
 * Implements {@link net.minecraftforge.client.extensions.IForgeBakedModel#getParticleTexture(IModelData)}.
 */
@Mixin(BlockModels.class)
public class MixinBlockModels implements ForgeBlockModels {
	@Redirect(method = "getSprite", at = @At(value = "INVOKE", ordinal = 0, target =
			"Lnet/minecraft/client/render/model/BakedModel;getSprite()Lnet/minecraft/client/texture/Sprite;"))
	private Sprite hook_getSprite(BakedModel bakedModel) {
		return ((IForgeBakedModel) bakedModel).getParticleTexture(EmptyModelData.INSTANCE);
	}

	@Override
	public Sprite getTexture(BlockState state, World world, BlockPos pos) {
		BlockModels me = (BlockModels) (Object) this;
		IModelData data = ModelDataManager.getModelData(world, pos);
		IForgeBakedModel bakedMode = (IForgeBakedModel) me.getModel(state);
		return bakedMode.getParticleTexture(data == null ? EmptyModelData.INSTANCE : data);
	}
}
