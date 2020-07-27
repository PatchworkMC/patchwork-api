/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.client.model.geometry;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.ModelLoader;

import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

/**
 * General interface for any model that can be baked, superset of vanilla
 * {@link net.minecraft.client.renderer.model.IUnbakedModel}. Models can be
 * baked to different vertex formats and with different state.
 */
public interface IModelGeometry<T extends IModelGeometry<T>> {
	default Collection<? extends IModelGeometryPart> getParts() {
		return Collections.emptyList();
	}

	default Optional<? extends IModelGeometryPart> getPart(String name) {
		return Optional.empty();
	}

	BakedModel bake(IModelConfiguration owner, ModelLoader bakery, Function<Identifier, Sprite> spriteGetter,
			ModelBakeSettings sprite, VertexFormat format, ModelItemPropertyOverrideList overrides);

	Collection<Identifier> getTextureDependencies(IModelConfiguration owner,
			Function<Identifier, UnbakedModel> modelGetter, Set<String> missingTextureErrors);
}