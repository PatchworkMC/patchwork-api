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

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.client.render.model.json.ModelItemOverride;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import net.patchworkmc.impl.modelloader.PatchworkJsonUnbakedModel;

public class ModelLoaderRegistry2 {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String WHITE_TEXTURE = "forge:white";
	private static final Map<Identifier, IModelLoader<?>> loaders = new HashMap<>();
	private static volatile boolean registryFrozen = false;

	public static void initComplete() {
		registryFrozen = true;
	}

	/**
	 * Makes system aware of your loader.
	 *
	 * <p>Actually, we need to register our IModelLoader in the Mod class constructor (Using a DistExecutor).
	 * Because FMLClientSetupEvent and ModelRegistryEvent are fired in paralleled with the model baking process,
	 * which may create a racing condition.
	 *
	 * <p>Forge team are aware of this. This is not a Forge bug.
	 */
	public static void registerLoader(Identifier id, IModelLoader<?> loader) {
		if (registryFrozen) {
			throw new IllegalStateException("Can not register model loaders after models have started loading. Please use FMLClientSetupEvent or ModelRegistryEvent to register your loaders.");
		}

		synchronized (loaders) {
			loaders.put(id, loader);
			((ReloadableResourceManager) MinecraftClient.getInstance().getResourceManager()).registerListener(loader);
		}
	}

	public static IModelGeometry<?> getModel(Identifier loaderId, JsonDeserializationContext deserializationContext, JsonObject data) {
		try {
			if (!loaders.containsKey(loaderId)) {
				throw new IllegalStateException(
						String.format("Model loader '%s' not found. Registered loaders: %s", loaderId,
								loaders.keySet().stream().map(Identifier::toString).collect(Collectors.joining(", "))));
			}

			IModelLoader<?> loader = loaders.get(loaderId);

			return loader.read(deserializationContext, data);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Nullable
	public static IModelGeometry<?> deserializeGeometry(JsonDeserializationContext deserializationContext,
			JsonObject object) {
		if (!object.has("loader")) {
			return null;
		}

		Identifier loader = new Identifier(JsonHelper.getString(object, "loader"));
		return getModel(loader, deserializationContext, object);
	}

	// Patchwork's own method
	public static GsonBuilder registerVanillaAdapters(GsonBuilder builder) {
		try {
			return builder
					.registerTypeAdapter(ModelElement.class, new ModelElement.Deserializer())
					.registerTypeAdapter(ModelElementFace.class, new ModelElementFace.Deserializer())
					.registerTypeAdapter(ModelElementTexture.class, new ModelElementTexture.Deserializer())
					.registerTypeAdapter(Transformation.class, new Transformation.Deserializer())
					.registerTypeAdapter(ModelTransformation.class, new ModelTransformation.Deserializer())
					.registerTypeAdapter(ModelItemOverride.class, new ModelItemOverride.Deserializer());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static class ExpandedBlockModelDeserializer extends JsonUnbakedModel.Deserializer {
		public static final Gson INSTANCE = registerVanillaAdapters(new GsonBuilder())
				.registerTypeAdapter(JsonUnbakedModel.class, new ExpandedBlockModelDeserializer())
				// .registerTypeAdapter(TRSRTransformation.class, ForgeBlockStateV1.TRSRDeserializer.INSTANCE)
				.create();

		public static JsonUnbakedModel deserializeExpanded(JsonElement element, Type targetType,
				JsonDeserializationContext deserializationContext, JsonUnbakedModel model) {
			JsonObject jsonobject = element.getAsJsonObject();
			IModelGeometry<?> geometry = deserializeGeometry(deserializationContext, jsonobject);

			List<ModelElement> elements = model.getElements();
			BlockModelConfiguration customData = ((PatchworkJsonUnbakedModel) model).getCustomData();

			if (geometry != null) {
				elements.clear();
				customData.setCustomGeometry(geometry);
			}

			// IModelState modelState = deserializeModelTransforms(deserializationContext, jsonobject);
			// if (modelState != null) {
			// 	customData.setCustomModelState(modelState);
			// }

			if (jsonobject.has("visibility")) {
				JsonObject visibility = JsonHelper.getObject(jsonobject, "visibility");

				for (Map.Entry<String, JsonElement> part : visibility.entrySet()) {
					// customData.visibilityData.setVisibilityState(part.getKey(), part.getValue().getAsBoolean());
				}
			}

			return model;
		}

		@Override
		public JsonUnbakedModel deserialize(JsonElement element, Type targetType,
				JsonDeserializationContext deserializationContext) throws JsonParseException {
			JsonUnbakedModel model = super.deserialize(element, targetType, deserializationContext);
			return deserializeExpanded(element, targetType, deserializationContext, model);
		}
	}
}
