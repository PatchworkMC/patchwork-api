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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import net.patchworkmc.impl.modelloader.SpecialModelProvider;

public class ModelLoader extends net.minecraft.client.render.model.ModelLoader implements SpecialModelProvider {
	private static final Marker MODELLOADING = MarkerManager.getMarker("MODELLOADING");
	private static Set<Identifier> specialModels = new HashSet<>();
	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<Identifier, Exception> loadingExceptions = new HashMap<>();
	private boolean isLoading = false;
	private static ModelLoader instance;

	@Nullable
	public static ModelLoader instance() {
		return instance;
	}

	public boolean isLoading() {
		return isLoading;
	}

	public ModelLoader(ResourceManager resourceManager, SpriteAtlasTexture spriteAtlas, BlockColors blockColors,
			Profiler profiler) {
		super(resourceManager, spriteAtlas, blockColors, profiler);
	}

	/**
	 * Indicate to vanilla that it should load and bake the given model, even if no
	 * blocks or items use it. This is useful if e.g. you have baked models only for
	 * entity renderers. Call during
	 * {@link net.minecraftforge.client.event.ModelRegistryEvent}
	 *
	 * @param rl The model, either {@link ModelResourceLocation} to point to a
	 *           blockstate variant, or plain {@link ResourceLocation} to point
	 *           directly to a json in the models folder.
	 */
	public static void addSpecialModel(Identifier rl) {
		specialModels.add(rl);
	}

	@Override
	public Set<Identifier> getSpecialModels() {
		return specialModels;
	}

	/**
	 * Internal, do not use.
	 */
	public void onPostBakeEvent(Map<Identifier, BakedModel> modelRegistry) {
		BakedModel missingModel = modelRegistry.get(MISSING);

		for (Map.Entry<Identifier, Exception> entry : loadingExceptions.entrySet()) {
			// ignoring pure Identifier arguments, all things we care about pass
			// ModelIdentifier
			if (entry.getKey() instanceof ModelIdentifier) {
				LOGGER.debug(MODELLOADING, "Model {} failed to load: {}", entry.getKey().toString(),
						entry.getValue().getLocalizedMessage());
				final ModelIdentifier location = (ModelIdentifier) entry.getKey();
				final BakedModel model = modelRegistry.get(location);

				if (model == null) {
					modelRegistry.put(location, missingModel);
				}
			}
		}

		loadingExceptions.clear();
		isLoading = false;
	}
}
