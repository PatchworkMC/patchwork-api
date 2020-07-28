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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import net.minecraftforge.client.model.BlockModelConfiguration;
import net.minecraftforge.client.model.ModelLoaderRegistry2;

import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelItemOverride;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

import net.patchworkmc.impl.modelloader.ModelItemPropertyOverrideListConstructor;
import net.patchworkmc.impl.modelloader.PatchworkJsonUnbakedModel;
import net.patchworkmc.impl.modelloader.PatchworkModelBakeContext;
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
	@Override
	public ModelItemPropertyOverrideList getOverrides(ModelLoader modelBakeryIn, JsonUnbakedModel modelIn,
			Function<Identifier, Sprite> textureGetter, VertexFormat format) {
		JsonUnbakedModel me = (JsonUnbakedModel) (Object) this;
		List<ModelItemOverride> overrides = me.getOverrides();
		return overrides.isEmpty()
				? ModelItemPropertyOverrideList.EMPTY
				: ModelItemPropertyOverrideListConstructor.construct(modelBakeryIn, modelIn, modelBakeryIn::getOrLoadModel, textureGetter, overrides, format);
	}

	////////////////////////////
	/// getTextureDependencies
	////////////////////////////
	// Set<Identifier> set2 = Sets.newHashSet(new Identifier[]{new Identifier(this.resolveTexture("particle"))});
	// Iterator var6 = this.getElements().iterator();
	@SuppressWarnings("unchecked")
	@Redirect(method = "getTextureDependencies", at = @At(value = "INVOKE", ordinal = 0, target = "com/google/common/collect/Sets.newHashSet([Ljava/lang/Object;)Ljava/util/HashSet;"))
	private HashSet getTextureDependencies_Sets_newHashSet(Object[] elements,
			Function<Identifier, UnbakedModel> unbakedModelGetter, Set<String> unresolvedTextureReferences) {
		HashSet textures = Sets.newHashSet(elements);

		if (customData.hasCustomGeometry()) {
			textures.addAll(customData.getTextureDependencies(unbakedModelGetter, unresolvedTextureReferences));
		}

		return textures;
	}

	@SuppressWarnings("rawtypes")
	@Redirect(method = "getTextureDependencies", at = @At(value = "INVOKE", ordinal = 0, target = Signatures.JsonUnbakedModel_getElements))
	private List getTextureDependencies_this_getElements(JsonUnbakedModel me) {
		// Skip the vanilla logic if this model has custom geometry.
		return customData.hasCustomGeometry() ? Collections.EMPTY_LIST : me.getElements();
	}

	////////////////////////////
	/// bake() stuff
	////////////////////////////
	@Overwrite
	@Override
	@Deprecated
	// No longer needed in 1.15
	public BakedModel bake(ModelLoader vanillaModelLoader, Function<Identifier, Sprite> textureGetter,
			ModelBakeSettings modelBakeSettings) {
		return bake(vanillaModelLoader, textureGetter, modelBakeSettings, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
	}

	// Declared in IForgeUnbakedModel, will be removed in 1.15 (replaced with the vanilla method).
	// In 1.14, this method is called throughout the code.
	@Override
	@Deprecated
	public BakedModel bake(ModelLoader vanillaModelLoader,
			Function<Identifier, Sprite> textureGetter, ModelBakeSettings modelBakeSettings, VertexFormat format) {
		JsonUnbakedModel me = (JsonUnbakedModel) (Object) this;
		return bake(vanillaModelLoader, me, textureGetter, modelBakeSettings, format);
	}

	// Forge add this method, no longer needed in 1.15.
	@Override
	@Deprecated
	public BakedModel bake(ModelLoader loader, JsonUnbakedModel parent, Function<Identifier, Sprite> textureGetter,
			ModelBakeSettings settings, VertexFormat format) {
		JsonUnbakedModel me = (JsonUnbakedModel) (Object) this;
		return ModelLoaderRegistry2.bakeHelper(me, loader, me, textureGetter, settings, format);
	}

	@Override
	public BakedModel bakeVanilla(ModelLoader loader, JsonUnbakedModel parent, Function<Identifier, Sprite> textureGetter,
			ModelBakeSettings settings, VertexFormat format) {
		bakeVanillaContext.setExtraParam(bakeVanillaMarker, format);
		return bake(loader, parent, textureGetter, settings);
	}

	// The vanilla bake function which contains the actual logic of Forge's bakeVanilla()
	@Shadow
	public abstract BakedModel bake(ModelLoader loader, JsonUnbakedModel parent,
			Function<Identifier, Sprite> textureGetter, ModelBakeSettings settings);

	@Unique
	private static final Function<Identifier, Sprite> bakeVanillaMarker = (dummy) -> null;
	@Unique
	private static final PatchworkModelBakeContext bakeVanillaContext = new PatchworkModelBakeContext();

	@Inject(method = Signatures.JsonUnbakedModel_bake, at = @At("HEAD"), cancellable = true)
	private void bake_head(ModelLoader loader, JsonUnbakedModel parent, Function<Identifier, Sprite> textureGetter,
			ModelBakeSettings settings, CallbackInfoReturnable<BakedModel> cir) {
		if (bakeVanillaContext.isExtraParamSet()) {
			// Called from ModelLoaderRegistry.bakeHelper, resume vanilla logic
			bakeVanillaContext.push(null, null);
		} else {
			// Redirect other calls to ModelLoaderRegistry.bakeHelper
			BakedModel bakedModel = bake(loader, parent, textureGetter, settings, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			cir.setReturnValue(bakedModel);
		}
	}

	@Inject(method = Signatures.JsonUnbakedModel_bake, at = @At("RETURN"))
	private void bake_return(CallbackInfoReturnable<BakedModel> cir) {
		bakeVanillaContext.pop();
	}
}
