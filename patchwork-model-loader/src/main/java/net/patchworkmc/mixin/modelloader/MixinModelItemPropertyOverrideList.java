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

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.google.common.collect.ImmutableList;

import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelItemOverride;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.util.Identifier;

import net.patchworkmc.impl.modelloader.ForgeModelLoader;
import net.patchworkmc.impl.modelloader.ModelItemPropertyOverrideListConstructor;
import net.patchworkmc.impl.modelloader.Signatures;

@Mixin(ModelItemPropertyOverrideList.class)
public abstract class MixinModelItemPropertyOverrideList {
	@Shadow
	@Final
	private List<ModelItemOverride> overrides;

	public ImmutableList<ModelItemOverride> getOverrides() {
		return ImmutableList.copyOf(overrides);
	}

	@Unique
	private static final String constructorSignature = "<init>("
			+ "Lnet/minecraft/client/render/model/ModelLoader;"
			+ "Lnet/minecraft/client/render/model/json/JsonUnbakedModel;"
			+ "Ljava/util/function/Function;Ljava/util/List;"
			+ ")V";

	// Due to the limitation of Mixin, we cannot @Inject to the head of the constructor.
	// This is a workaround.
	@SuppressWarnings("rawtypes")
	@Redirect(method = constructorSignature, at = @At(value = "INVOKE", ordinal = 0, target = "java/util/List.stream()Ljava/util/stream/Stream;"))
	private Stream constructor_Head(List list, ModelLoader modelLoader,
			JsonUnbakedModel unbakedModel, Function<Identifier, UnbakedModel> unbakedModelGetter, List<ModelItemOverride> overrides) {
		ModelItemPropertyOverrideListConstructor.context.push(
				((ForgeModelLoader) modelLoader).getSpriteMap()::getSprite,
				VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
		return list.stream();
	}

	@Redirect(method = "method_3496", at = @At(value = "INVOKE", target = Signatures.ModelLoader_bake))
	private static BakedModel bake(ModelLoader modelLoader, Identifier identifier, ModelBakeSettings settings) {
		return ((ForgeModelLoader) modelLoader).getBakedModel(identifier, settings,
						ModelItemPropertyOverrideListConstructor.context.textureGetter(),
						ModelItemPropertyOverrideListConstructor.context.vertexFormat());
	}

	@Inject(method = constructorSignature, at = @At("RETURN"))
	private void constructor_Return(CallbackInfo ci) {
		ModelItemPropertyOverrideListConstructor.context.pop();
	}
}
