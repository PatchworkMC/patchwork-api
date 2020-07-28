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
import java.util.Set;
import java.util.function.Function;

import net.minecraftforge.client.model.IModelConfiguration;

import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;

public interface IModelGeometryPart {
	String name();

	// void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelLoader bakery,
	//		Function<Identifier, Sprite> spriteGetter, ModelBakeSettings sprite, VertexFormat format);

	default Collection<Identifier> getTextureDependencies(IModelConfiguration owner,
			Function<Identifier, UnbakedModel> modelGetter, Set<String> missingTextureErrors) {
		// No texture dependencies
		return Collections.emptyList();
	}
}
