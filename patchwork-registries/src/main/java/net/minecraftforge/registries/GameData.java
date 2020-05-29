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
	/**
	 * Check a name for a domain prefix, and if not present infer it from the
	 * current active mod container.
	 *
	 * @param name          The name or resource location
	 * @param warnOverrides If false, the prefix is forcefully updated without a warning,
	 *                      and if true, the prefix is not updated and there is just a
	 *                      warning message.
	 * @return The {@link Identifier} with given or inferred domain
	 */
	public static Identifier checkPrefix(String name, boolean warnOverrides) {
		int colonIndex = name.lastIndexOf(':');

		if (colonIndex == -1) {
			String prefix = ModLoadingContext.get().getActiveNamespace();

			return new Identifier(prefix, name);
		}

		String oldPrefix = name.substring(0, colonIndex).toLowerCase(Locale.ROOT);

		String newName = name.substring(colonIndex + 1);
		String prefix = ModLoadingContext.get().getActiveNamespace();

		if (warnOverrides && !oldPrefix.equals(prefix) && oldPrefix.length() > 0) {
			LogManager.getLogger().info("Potentially Dangerous alternative prefix `{}` for name `{}`, expected `{}`. This could be a intended override, but in most cases indicates a broken mod.", oldPrefix, name, prefix);
			prefix = oldPrefix;
		}

		return new Identifier(prefix, newName);
	}
}
