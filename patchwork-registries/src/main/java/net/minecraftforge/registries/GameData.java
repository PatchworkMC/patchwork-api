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

package net.minecraftforge.registries;

import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import net.minecraftforge.fml.ModLoadingContext;

import net.minecraft.util.Identifier;

public class GameData {
	public static Identifier checkPrefix(String name, boolean warnOverrides) {
		int index = name.lastIndexOf(':');
		String oldPrefix = index == -1 ? "" : name.substring(0, index).toLowerCase(Locale.ROOT);
		name = index == -1 ? name : name.substring(index + 1);
		String prefix = ModLoadingContext.get().getActiveNamespace();

		if (warnOverrides && !oldPrefix.equals(prefix) && oldPrefix.length() > 0) {
			LogManager.getLogger().info(
					"Potentially Dangerous alternative prefix `{}` for name `{}`, expected `{}`. This could be a intended override, but in most cases indicates a broken mod.",
					oldPrefix, name, prefix
			);
			prefix = oldPrefix;
		}

		return new Identifier(prefix, name);
	}
}
