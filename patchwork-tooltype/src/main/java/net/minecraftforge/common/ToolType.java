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

package net.minecraftforge.common;

import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;

public final class ToolType {
	private static final Pattern VALID_NAME = Pattern.compile("[^a-z_]"); //Only a-z and _ are allowed, meaning names must be lower case and use _ to separate words.
	private static final Map<String, ToolType> values = new HashMap<>();

	public static final ToolType AXE = get("axe");
	public static final ToolType PICKAXE = get("pickaxe");
	public static final ToolType SHOVEL = get("shovel");
	private final String name;

	private ToolType(String name) {
		this.name = name;
	}

	public static ToolType get(String name) {
		if (VALID_NAME.matcher(name).find()) {
			throw new IllegalArgumentException("ToolType.get() called with invalid name: " + name);
		}

		return values.computeIfAbsent(name, k -> new ToolType(name));
	}

	public String getName() {
		return name;
	}
}
