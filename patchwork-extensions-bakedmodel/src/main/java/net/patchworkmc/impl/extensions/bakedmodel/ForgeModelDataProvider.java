package net.patchworkmc.impl.extensions.bakedmodel;

import javax.annotation.Nonnull;

import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

public interface ForgeModelDataProvider {
	/**
	 * Allows you to return additional model data.
	 * This data can be used to provide additional functionality in your {@link net.minecraft.client.renderer.model.IBakedModel}
	 * You need to schedule a refresh of you model data via {@link #requestModelDataUpdate()} if the result of this function changes.
	 * <b>Note that this method may be called on a chunk render thread instead of the main client thread</b>
	 *
	 * @return Your model data
	 */
	@Nonnull
	default IModelData getModelData() {
		return EmptyModelData.INSTANCE;
	}
}
