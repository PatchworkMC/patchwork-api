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

import net.minecraft.item.Item;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class TagEmptyCondition implements ICondition {
	private static final Identifier NAME = new Identifier("forge", "tag_empty");
	private final Identifier tag_name;

	public TagEmptyCondition(String location) {
		this(new Identifier(location));
	}

	public TagEmptyCondition(String namespace, String path) {
		this(new Identifier(namespace, path));
	}

	public TagEmptyCondition(Identifier tag) {
		this.tag_name = tag;
	}

	@Override
	public Identifier getID() {
		return NAME;
	}

	@Override
	public boolean test() {
		Tag<Item> tag = ItemTags.getTagGroup().get(tag_name);
		return tag == null || tag.values().isEmpty();
	}

	@Override
	public String toString() {
		return "tag_empty(\"" + tag_name + "\")";
	}

	public static class Serializer implements IConditionSerializer<TagEmptyCondition> {
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public void write(JsonObject json, TagEmptyCondition value) {
			json.addProperty("tag", value.tag_name.toString());
		}

		@Override
		public TagEmptyCondition read(JsonObject json) {
			return new TagEmptyCondition(new Identifier(JsonHelper.getString(json, "tag")));
		}

		@Override
		public Identifier getID() {
			return TagEmptyCondition.NAME;
		}
	}
}
