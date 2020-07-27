package net.minecraftforge.client.model;

import javax.annotation.Nullable;

import net.minecraftforge.client.model.geometry.IModelGeometry;

import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.JsonUnbakedModel;

import net.patchworkmc.impl.modelloader.PatchworkJsonUnbakedModel;

public class BlockModelConfiguration implements IModelConfiguration {
	public final JsonUnbakedModel owner;
//    public final VisibilityData visibilityData = new VisibilityData();
	@Nullable
	private IModelGeometry<?> customGeometry;
//    @Nullable
//    private IModelState customModelState;

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

	@Override
	public boolean isTexturePresent(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String resolveTexture(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isShadedInGui() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean useSmoothLighting() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ModelTransformation getCameraTransforms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ModelBakeSettings getCombinedTransform() {
		// TODO Auto-generated method stub
		return null;
	}

	public void copyFrom(BlockModelConfiguration other) {
		this.customGeometry = other.customGeometry;
		// this.customModelState = other.customModelState;
		// this.visibilityData.copyFrom(other.visibilityData);
	}
}
