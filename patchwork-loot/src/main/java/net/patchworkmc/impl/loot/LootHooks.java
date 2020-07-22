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

package net.patchworkmc.impl.loot;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.apache.commons.lang3.mutable.MutableInt;

import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import net.patchworkmc.impl.event.loot.LootEvents;

public class LootHooks {
	public static void prepareLootTable(Identifier id, JsonObject lootTableObj, boolean custom, LootManager lootTableManager) {
		// passing pool names into loot pool deserialization mixin since we have all the information we need right now, but not later
		if (lootTableObj.has("pools")) {
			MutableInt poolCount = new MutableInt(0);
			JsonArray pools = lootTableObj.getAsJsonArray("pools");

			for (JsonElement elem: pools) {
				JsonObject pool = JsonHelper.asObject(elem, "loot pool");
				String name = getPoolName(pool, id, custom, poolCount);
				pool.addProperty("name", name);
			}
		}

		// we don't call GSON here because we're letting vanilla do it in a bit.
		// ForgeHooks implementation wraps this, performing the deserialization
	}

	// More or less a copy of ForgeHooks.readPoolName. Actual implementation of that will just pull the name
	// from the json, since prepareLootTable is taking the name from this and putting it in the json.
	private static String getPoolName(JsonObject lootPoolObj, Identifier id, boolean custom, MutableInt poolCount) {
		boolean vanilla = "minecraft".equals(id.getNamespace());

		if (lootPoolObj.has("name")) {
			return JsonHelper.getString(lootPoolObj, "name");
		}

		if (custom) {
			return "custom#" + lootPoolObj.hashCode(); //We don't care about custom ones modders shouldn't be editing them!
		}

		poolCount.increment();

		if (!vanilla) {
			throw new JsonParseException("Loot Table \"" + ctx.name.toString() + "\" Missing `name` entry for pool #" + (ctx.poolCount - 1));
		}

		return poolCount.intValue() == 1 ? "main" : "pool" + (poolCount.intValue() - 1);
	}

	// TODO: should we move this implementation to ForgeHooks? since this is only going to be called by ForgeHooks.
	public static LootTable loadLootTable(Gson gson, Identifier name, JsonElement data, boolean custom, LootManager lootTableManager) {
		JsonObject lootTableObj = JsonHelper.asObject(data, "loot table");

		// This accomplishes the stashing of data that Forge does, without needing a threadlocal static
		prepareLootTable(name, lootTableObj, custom, lootTableManager);

		// TODO: Can we do something like call the lambda method directly?
		//       As is, we're gonna duplicate the stuff we add via mixin.

		LootTable ret = gson.fromJson(data, LootTable.class);

		if (!custom) {
			ret = LootEvents.loadLootTable(name, ret, lootTableManager);
		}

		// if (ret != null) {
		// 	ret.freeze();
		// }

		return ret;
	}

	// Replacement for ForgeHooks.readPoolName: since we prepared the json object, the name is already present.
	public static String readPoolName(JsonObject lootPoolObj) {
		// TODO: throw a better exception?
		return JsonHelper.getString(lootPoolObj, "name");
	}
}
