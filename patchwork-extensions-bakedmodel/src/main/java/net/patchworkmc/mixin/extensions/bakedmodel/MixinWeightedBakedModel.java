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

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraftforge.client.extensions.IForgeBakedModel;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;

/**
 * Implements {@link IForgeBakedModel#isAmbientOcclusion()}.
 * Patch: https://github.com/PatchworkMC/YarnForge/blob/1.14.x/patches/minecraft/net/minecraft/client/render/model/WeightedBakedModel.java.patch
 */
@Mixin(WeightedBakedModel.class)
public abstract class MixinWeightedBakedModel implements IForgeBakedModel {
	@Shadow
	@Final
	private BakedModel defaultModel;

	@Override
	public boolean isAmbientOcclusion(BlockState state) {
		return ((IForgeBakedModel) defaultModel).isAmbientOcclusion(state);
	}
}
