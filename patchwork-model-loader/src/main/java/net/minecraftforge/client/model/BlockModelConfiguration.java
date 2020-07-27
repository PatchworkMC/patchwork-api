package net.minecraftforge.client.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;

import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;

import net.patchworkmc.impl.modelloader.PatchworkJsonUnbakedModel;

public class BlockModelConfiguration implements IModelConfiguration {
	public final JsonUnbakedModel owner;
	public final VisibilityData visibilityData = new VisibilityData();

	@Nullable
	private IModelGeometry<?> customGeometry;
	// @Nullable
	// private IModelState customModelState; // ModelBakeSettings

	public BlockModelConfiguration(JsonUnbakedModel owner) {
		this.owner = owner;
	}

	@Nullable
	@Override
	public UnbakedModel getOwnerModel() {
		return owner;
	}

	@Override
	public String getModelName() {
		return owner.id;
	}

	public boolean hasCustomGeometry() {
		return getCustomGeometry() != null;
	}

	@Nullable
	public IModelGeometry<?> getCustomGeometry() {
		PatchworkJsonUnbakedModel pwOwner = (PatchworkJsonUnbakedModel) owner;
		PatchworkJsonUnbakedModel parent = (PatchworkJsonUnbakedModel) pwOwner.getParent();
		return parent != null && customGeometry == null ? parent.getCustomData().getCustomGeometry() : customGeometry;
	}

	public void setCustomGeometry(IModelGeometry<?> geometry) {
		this.customGeometry = geometry;
	}

	// @Nullable
	// public IModelState getCustomModelState() {
	//	return owner.parent != null && customModelState == null ? owner.parent.customData.getCustomModelState()
	//			: customModelState;
	// }

	// public void setCustomModelState(IModelState modelState) {
	//	this.customModelState = modelState;
	// }

	@Override
	public boolean getPartVisibility(IModelGeometryPart part, boolean fallback) {
		PatchworkJsonUnbakedModel pwOwner = (PatchworkJsonUnbakedModel) owner;
		PatchworkJsonUnbakedModel parent = (PatchworkJsonUnbakedModel) pwOwner.getParent();

		return parent != null && !visibilityData.hasCustomVisibility(part)
				? parent.getCustomData().getPartVisibility(part, fallback)
				: visibilityData.isVisible(part, fallback);
	}

	@Override
	public boolean isTexturePresent(String name) {
		return owner.textureExists(name);
	}

	@Override
	public String resolveTexture(String name) {
		return owner.resolveTexture(name);
	}

	@Override
	public boolean isShadedInGui() {
		return owner.hasDepthInGui();
	}

	@Override
	public boolean useSmoothLighting() {
		return owner.useAmbientOcclusion();
	}

	@Override
	public ModelTransformation getCameraTransforms() {
		return owner.getTransformations();
	}

	// @Override
	// public IModelState getCombinedState() {
	//	IModelState state = getCustomModelState();
	//	return state != null
	//			? new SimpleModelState(PerspectiveMapWrapper.getTransformsWithFallback(state, getCameraTransforms()),state.apply(Optional.empty()))
	//			: new SimpleModelState(PerspectiveMapWrapper.getTransforms(getCameraTransforms()));
	// }

	public void copyFrom(BlockModelConfiguration other) {
		this.customGeometry = other.customGeometry;
		// this.customModelState = other.customModelState;
		this.visibilityData.copyFrom(other.visibilityData);
	}

	public Collection<Identifier> getTextureDependencies(Function<Identifier, UnbakedModel> modelGetter, Set<String> missingTextureErrors) {
		IModelGeometry<?> geometry = getCustomGeometry();
		return geometry == null ? Collections.emptySet()
				: geometry.getTextureDependencies(this, modelGetter, missingTextureErrors);
	}

	public BakedModel bake(net.minecraft.client.render.model.ModelLoader bakery,
			Function<Identifier, Sprite> bakedTextureGetter, ModelBakeSettings sprite, VertexFormat format,
			ModelItemPropertyOverrideList overrides) {
		IModelGeometry<?> geometry = getCustomGeometry();

		if (geometry == null) {
			throw new IllegalStateException("Can not use custom baking without custom geometry");
		}

		return geometry.bake(this, bakery, bakedTextureGetter, sprite, format, overrides);
	}

	public static class VisibilityData {
		private final Map<String, Boolean> data = new HashMap<>();

		public boolean hasCustomVisibility(IModelGeometryPart part) {
			return data.containsKey(part.name());
		}

		public boolean isVisible(IModelGeometryPart part, boolean fallback) {
			return data.getOrDefault(part.name(), fallback);
		}

		public void setVisibilityState(String partName, boolean type) {
			data.put(partName, type);
		}

		public void copyFrom(VisibilityData visibilityData) {
			data.clear();
			data.putAll(visibilityData.data);
		}
	}
}
