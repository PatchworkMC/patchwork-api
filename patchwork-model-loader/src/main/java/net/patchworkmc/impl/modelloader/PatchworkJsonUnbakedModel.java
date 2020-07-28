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

package net.patchworkmc.impl.modelloader;

import java.util.function.Function;

import net.minecraftforge.client.model.BlockModelConfiguration;
import net.minecraftforge.client.model.IForgeUnbakedModel;

import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

public interface PatchworkJsonUnbakedModel extends UnbakedModel, IForgeUnbakedModel {
	JsonUnbakedModel getParent();

	BlockModelConfiguration getCustomData();

	// TODO: param VertexFormat is removed in 1.15
	ModelItemPropertyOverrideList getOverrides(ModelLoader modelBakeryIn, JsonUnbakedModel modelIn,
			Function<Identifier, Sprite> textureGetter, VertexFormat format);

	/**
	 * Forge's method, removed in 1.15.
	 */
	@Deprecated
	BakedModel bake(ModelLoader loader, JsonUnbakedModel parent, Function<Identifier, Sprite> textureGetter,
			ModelBakeSettings settings, VertexFormat format);

	/**
	 * Exposed for Forge's internal use.
	 *
	 * <p>Perform the vanilla bake logic. Called by bakeHelper() if the supplied model does not have custom IModelGeometry.
	 */
	BakedModel bakeVanilla(ModelLoader loader, JsonUnbakedModel parent, Function<Identifier, Sprite> textureGetter,
			ModelBakeSettings settings, VertexFormat format);
}
