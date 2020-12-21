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

package net.minecraftforge.common.crafting.conditions;

import com.google.gson.JsonObject;

import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class ItemExistsCondition implements ICondition {
	private static final Identifier NAME = new Identifier("forge", "item_exists");
	private final Identifier item;

	public ItemExistsCondition(String location) {
		this(new Identifier(location));
	}

	public ItemExistsCondition(String namespace, String path) {
		this(new Identifier(namespace, path));
	}

	public ItemExistsCondition(Identifier item) {
		this.item = item;
	}

	@Override
	public Identifier getID() {
		return NAME;
	}

	@Override
	public boolean test() {
		return Registry.ITEM.containsId(item);
	}

	@Override
	public String toString() {
		return "item_exists(\"" + item + "\")";
	}

	public static class Serializer implements IConditionSerializer<ItemExistsCondition> {
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public void write(JsonObject json, ItemExistsCondition value) {
			json.addProperty("item", value.item.toString());
		}

		@Override
		public ItemExistsCondition read(JsonObject json) {
			return new ItemExistsCondition(new Identifier(JsonHelper.getString(json, "item")));
		}

		@Override
		public Identifier getID() {
			return ItemExistsCondition.NAME;
		}
	}
}
