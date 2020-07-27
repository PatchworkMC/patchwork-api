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

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.google.gson.Gson;
import net.minecraftforge.client.model.BlockModelConfiguration;
import net.minecraftforge.client.model.ModelLoaderRegistry2;

import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelItemOverride;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

import net.patchworkmc.impl.modelloader.ModelItemPropertyOverrideListConstructor;
import net.patchworkmc.impl.modelloader.PatchworkJsonUnbakedModel;
import net.patchworkmc.impl.modelloader.Signatures;

@Mixin(JsonUnbakedModel.class)
public abstract class MixinJsonUnbakedModel implements PatchworkJsonUnbakedModel {
	@Shadow
	protected JsonUnbakedModel parent;
	@Shadow
	protected Identifier parentId;

	@Unique
	public final BlockModelConfiguration customData = new BlockModelConfiguration((JsonUnbakedModel) (Object) this);

	@Override
	public JsonUnbakedModel getParent() {
		return parent;
	}

	@Override
	public BlockModelConfiguration getCustomData() {
		return customData;
	}

	@ModifyArg(
			method = "deserialize(Ljava/io/Reader;)Lnet/minecraft/client/render/model/json/JsonUnbakedModel;",
			at = @At(
					value = "INVOKE",
					target = Signatures.JsonHelper_deserialize
					)
			)
	private static Gson replaceGSON(Gson gsonIn) {
		return ModelLoaderRegistry2.ExpandedBlockModelDeserializer.INSTANCE;
	}

	@Inject(method = "getElements", at = @At("HEAD"))
	private void getElements_Head(CallbackInfoReturnable<List<ModelElement>> ci) {
		if (this.getCustomData().hasCustomGeometry()) {
			ci.setReturnValue(Collections.emptyList());
		}
	}

	@Nullable
	public Identifier getParentLocation() {
		return parentId;
	}

	// TODO: param VertexFormat is removed in 1.15
	public ModelItemPropertyOverrideList getOverrides(ModelLoader modelBakeryIn, JsonUnbakedModel modelIn,
			Function<Identifier, Sprite> textureGetter, VertexFormat format) {
		JsonUnbakedModel me = (JsonUnbakedModel) (Object) this;
		List<ModelItemOverride> overrides = me.getOverrides();
		return overrides.isEmpty()
				? ModelItemPropertyOverrideList.EMPTY
				: ModelItemPropertyOverrideListConstructor.construct(modelBakeryIn, modelIn, modelBakeryIn::getOrLoadModel, textureGetter, overrides, format);
	}
}
