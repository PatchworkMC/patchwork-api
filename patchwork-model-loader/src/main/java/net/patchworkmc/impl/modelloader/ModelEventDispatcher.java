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

import java.util.Map;

import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.ModLoader;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.util.Identifier;

public class ModelEventDispatcher {
	/**
	 * In Forge, ModelRegistryEvent is fired in parallel with FMLClientSetupEvent.
	 * Here we fire ModelRegistryEvent before FMLClientSetupEvent.
	 * The official forge does not set the ModLoadingContext here, so this should be fine.
	 */
	public static void fireModelRegistryEvent() {
		ModLoader.get().postEvent(new ModelRegistryEvent());
	}

	public static void onModelBake(BakedModelManager modelManager, Map<Identifier, BakedModel> modelRegistry, ModelLoader modelLoader) {
		ModLoader.get().postEvent(new ModelBakeEvent(modelManager, modelRegistry, modelLoader));
		modelLoader.onPostBakeEvent(modelRegistry);
	}
}
