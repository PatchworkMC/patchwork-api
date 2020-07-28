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

package net.minecraftforge.client.model;

import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

// This interface is moved to net.minecraftforge.client.extensions in 1.15
public interface IForgeUnbakedModel {
	/**
	 * Removed in 1.15, VertexFormat will be removed from all bake related functions.
	 * @param textureGetter		Where textures will be looked up when baking
	 * @param modelBakeSettings	Transforms to apply while baking. Usually will be an instance of {@link IModelState}.
	 */
	@Deprecated
	@Nullable
	BakedModel bake(net.minecraft.client.render.model.ModelLoader vanillaModelLoader,
			Function<Identifier, Sprite> textureGetter, ModelBakeSettings modelBakeSettings, VertexFormat format);

	/**
	 * Retrieves information about an animation clip in the model.
	 *
	 * @param name The clip name
	 * @return
	 */
	// default Optional<? extends IClip> getClip(String name) {
	//	return Optional.empty();
	// }
}
