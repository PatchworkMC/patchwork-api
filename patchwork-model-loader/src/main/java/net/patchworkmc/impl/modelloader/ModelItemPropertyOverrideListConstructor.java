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

import java.util.List;
import java.util.function.Function;

import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelItemOverride;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

public class ModelItemPropertyOverrideListConstructor {
	public static final PatchworkModelBakeContext context = new PatchworkModelBakeContext();

	/**
	 * Construct a ModelItemPropertyOverrideList with a custom textureGetter and VertexFormat.
	 *
	 * <p>This should be a constructor of ModelItemPropertyOverrideList, but with mixin, we cannot add it.
	 * We haven't seen any Forge mod calls this constructor directly.
	 */
	public static ModelItemPropertyOverrideList construct(ModelLoader modelLoader, JsonUnbakedModel unbakedModel, Function<Identifier, UnbakedModel> unbakedModelGetter, Function<Identifier, Sprite> textureGetter, List<ModelItemOverride> overrides, VertexFormat format) {
		context.setExtraParam(textureGetter, format);
		return new ModelItemPropertyOverrideList(modelLoader, unbakedModel, unbakedModelGetter, overrides);
	}
}
