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

package net.minecraftforge.common.crafting;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class CraftingHelper {
	private static final Map<Identifier, IConditionSerializer<?>> conditions = new HashMap<>();

	public static IConditionSerializer<?> register(IConditionSerializer<?> serializer) {
		Identifier key = serializer.getID();

		if (conditions.containsKey(key)) {
			throw new IllegalStateException("Duplicate recipe condition serializer: " + key);
		}

		conditions.put(key, serializer);
		return serializer;
	}

	public static boolean processConditions(JsonObject json, String memberName) {
		return !json.has(memberName) || processConditions(JsonHelper.getArray(json, memberName));
	}

	public static boolean processConditions(JsonArray conditions) {
		for (int x = 0; x < conditions.size(); x++) {
			if (!conditions.get(x).isJsonObject()) {
				throw new JsonSyntaxException("Conditions must be an array of JsonObjects");
			}

			JsonObject json = conditions.get(x).getAsJsonObject();

			if (!CraftingHelper.getCondition(json).test()) {
				return false;
			}
		}

		return true;
	}

	public static ICondition getCondition(JsonObject json) {
		Identifier type = new Identifier(JsonHelper.getString(json, "type"));
		IConditionSerializer<?> serializer = conditions.get(type);

		if (serializer == null) {
			throw new JsonSyntaxException("Unknown condition type: " + type.toString());
		}

		return serializer.read(json);
	}

	public static <T extends ICondition> JsonObject serialize(T condition) {
		@SuppressWarnings("unchecked")
		IConditionSerializer<T> serializer = (IConditionSerializer<T>) conditions.get(condition.getID());

		if (serializer == null) {
			throw new JsonSyntaxException("Unknown condition type: " + condition.getID().toString());
		}

		return serializer.getJson(condition);
	}
}
