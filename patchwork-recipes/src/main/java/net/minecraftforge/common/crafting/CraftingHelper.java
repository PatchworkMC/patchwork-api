package net.minecraftforge.common.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import java.util.HashMap;
import java.util.Map;

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


	public static <T extends ICondition> JsonObject serialize(T condition)
	{
		@SuppressWarnings("unchecked")
		IConditionSerializer<T> serializer = (IConditionSerializer<T>)conditions.get(condition.getID());
		if (serializer == null)
			throw new JsonSyntaxException("Unknown condition type: " + condition.getID().toString());
		return serializer.getJson(condition);
	}
}
