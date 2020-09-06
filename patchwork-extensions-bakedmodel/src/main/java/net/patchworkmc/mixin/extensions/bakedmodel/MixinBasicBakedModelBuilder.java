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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraftforge.client.extensions.IForgeBakedModel;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.texture.Sprite;

/**
 * Implements {@link IForgeBakedModel#isAmbientOcclusion()}.
 * Patch: https://github.com/PatchworkMC/YarnForge/blob/1.14.x/patches/minecraft/net/minecraft/client/render/model/BasicBakedModel.java.patch
 */
@Mixin(BasicBakedModel.Builder.class)
public abstract class MixinBasicBakedModelBuilder {
	@Redirect(method = "<init>(Lnet/minecraft/block/BlockState;Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/client/texture/Sprite;Ljava/util/Random;J)V",
			at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/render/model/BakedModel;useAmbientOcclusion()Z"))
	private static boolean useAmbientOcclusion(BakedModel bakedModelParam, BlockState state, BakedModel bakedModel, Sprite sprite, Random random, long randomSeed) {
		return ((IForgeBakedModel) bakedModel).isAmbientOcclusion(state);
	}
}
