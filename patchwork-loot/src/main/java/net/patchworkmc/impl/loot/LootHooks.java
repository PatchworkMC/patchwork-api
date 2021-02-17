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

import java.util.Deque;
import java.util.HashSet;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import net.patchworkmc.impl.event.loot.LootEvents;

// NOTE: this class is more or less a direct copy of parts of Forge's ForgeHooks.
public class LootHooks {
	// Made public for Patchwork's own use
	public static ThreadLocal<Deque<LootTableContext>> lootContext = new ThreadLocal<Deque<LootTableContext>>();

	public static LootTable loadLootTable(Gson gson, Identifier name, JsonElement data, boolean custom, LootManager lootTableManager) {
		Deque<LootTableContext> que = lootContext.get();

		if (que == null) {
			que = Queues.newArrayDeque();
			lootContext.set(que);
		}

		LootTable ret = null;

		try {
			que.push(new LootTableContext(name, custom));
			ret = gson.fromJson(data, LootTable.class);
			que.pop();
		} catch (JsonParseException e) {
			que.pop();
			throw e;
		}

		if (!custom) {
			ret = LootEvents.loadLootTable(name, ret, lootTableManager);
		}

		// if (ret != null) {
		// 	ret.freeze();
		// }

		return ret;
	}

	private static LootTableContext getLootTableContext() {
		LootTableContext ctx = lootContext.get().peek();

		if (ctx == null) {
			throw new JsonParseException("Invalid call stack, could not grab json context!"); // Should I throw this? Do we care about custom deserializers outside the manager?
		}

		return ctx;
	}

	public static String readPoolName(JsonObject json) {
		LootTableContext ctx = LootHooks.getLootTableContext();
		ctx.resetPoolCtx();

		if (json.has("name")) {
			return JsonHelper.getString(json, "name");
		}

		if (ctx.custom) {
			return "custom#" + json.hashCode(); //We don't care about custom ones modders shouldn't be editing them!
		}

		ctx.poolCount++;

		if (!ctx.vanilla) {
			throw new JsonParseException("Loot Table \"" + ctx.name.toString() + "\" Missing `name` entry for pool #" + (ctx.poolCount - 1));
		}

		return ctx.poolCount == 1 ? "main" : "pool" + (ctx.poolCount - 1);
	}

	// Made public for Patchwork's own use
	public static class LootTableContext {
		public final Identifier name;
		public final boolean custom;
		private final boolean vanilla;
		public int poolCount = 0;
		public int entryCount = 0;
		private HashSet<String> entryNames = Sets.newHashSet();

		protected LootTableContext(Identifier name, boolean custom) {
			this.name = name;
			this.custom = custom;
			this.vanilla = "minecraft".equals(this.name.getNamespace());
		}

		private void resetPoolCtx() {
			this.entryCount = 0;
			this.entryNames.clear();
		}

		public String validateEntryName(@Nullable String name) {
			if (name != null && !this.entryNames.contains(name)) {
				this.entryNames.add(name);
				return name;
			}

			if (!this.vanilla) {
				throw new JsonParseException("Loot Table \"" + this.name.toString() + "\" Duplicate entry name \"" + name + "\" for pool #" + (this.poolCount - 1) + " entry #" + (this.entryCount - 1));
			}

			int x = 0;

			while (this.entryNames.contains(name + "#" + x)) {
				x++;
			}

			name = name + "#" + x;
			this.entryNames.add(name);

			return name;
		}
	}
}
