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

package net.patchworkmc.impl.extensions.bakedmodel;

import org.jetbrains.annotations.NotNull;

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
	@NotNull
	default IModelData getModelData() {
		return EmptyModelData.INSTANCE;
	}
}
