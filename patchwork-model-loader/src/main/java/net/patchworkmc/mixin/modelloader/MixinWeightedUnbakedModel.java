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

package net.patchworkmc.mixin.modelloader;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraftforge.client.model.IForgeUnbakedModel;

import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

import net.patchworkmc.impl.modelloader.ForgeModelLoader;
import net.patchworkmc.impl.modelloader.PatchworkModelBakeContext;
import net.patchworkmc.impl.modelloader.Signatures;

@Mixin(WeightedUnbakedModel.class)
public abstract class MixinWeightedUnbakedModel implements IForgeUnbakedModel {
	@Unique
	private static final PatchworkModelBakeContext bakeContext = new PatchworkModelBakeContext();

	@Inject(method = "bake", at = @At("HEAD"))
	private void bake_Head(ModelLoader loader, Function<Identifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, CallbackInfoReturnable<BakedModel> cir) {
		bakeContext.push(textureGetter, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
	}

	@Redirect(method = "bake", at = @At(value = "INVOKE", target = Signatures.ModelLoader_bake))
	private BakedModel ModelLoader_bake_Redirect(
			ModelLoader modelLoader, Identifier identifier, ModelBakeSettings settings,
			ModelLoader loader, Function<Identifier, Sprite> textureGetter, ModelBakeSettings rotationContainer) {
		return ((ForgeModelLoader) modelLoader).getBakedModel(identifier, settings, textureGetter, bakeContext.vertexFormat());
	}

	@Inject(method = "bake", at = @At("RETURN"))
	private void bake_Return(CallbackInfoReturnable<BakedModel> cir) {
		bakeContext.pop();
	}

	@Override
	public BakedModel bake(ModelLoader modelLoader, Function<Identifier, Sprite> textureGetter, ModelBakeSettings settings, VertexFormat format) {
		bakeContext.setExtraParam(textureGetter, format);
		return ((WeightedUnbakedModel) (Object) this).bake(modelLoader, textureGetter, settings);
	}
}
