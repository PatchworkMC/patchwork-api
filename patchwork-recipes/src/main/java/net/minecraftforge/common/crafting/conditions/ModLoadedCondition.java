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

import net.fabricmc.loader.api.FabricLoader;

public class ModLoadedCondition implements ICondition {
	private static final Identifier NAME = new Identifier("forge", "mod_loaded");
	private final String modid;

	public ModLoadedCondition(String modid) {
		this.modid = modid;
	}

	@Override
	public Identifier getID() {
		return NAME;
	}

	@Override
	public boolean test() {
		return FabricLoader.getInstance().isModLoaded(modid);
	}

	@Override
	public String toString() {
		return "mod_loaded(\"" + modid + "\")";
	}

	public static class Serializer implements IConditionSerializer<ModLoadedCondition> {
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public void write(JsonObject json, ModLoadedCondition value) {
			json.addProperty("modid", value.modid);
		}

		@Override
		public ModLoadedCondition read(JsonObject json) {
			return new ModLoadedCondition(JsonHelper.getString(json, "modid"));
		}

		@Override
		public Identifier getID() {
			return ModLoadedCondition.NAME;
		}
	}
}
