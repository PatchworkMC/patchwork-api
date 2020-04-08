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

package net.patchworkmc.impl.worldtypes;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import net.minecraft.world.level.LevelGeneratorType;

import net.fabricmc.loader.api.FabricLoader;

public class PatchworkLevelGeneratorTypes {
	private static final Field TYPES;

	public static int getNextID() {
		LevelGeneratorType[] types;

		try {
			types = (LevelGeneratorType[]) TYPES.get(null);

			for (int x = 0; x < types.length; x++) {
				if (types[x] == null) {
					return x;
				}
			}

			int old = types.length;
			TYPES.set(null, Arrays.copyOf(types, old + 16));
			return old;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	static {
		try {
			TYPES = LevelGeneratorType.class.getDeclaredField(FabricLoader.getInstance().isDevelopmentEnvironment() ? "TYPES" : "field_9279");
			// make accessible
			TYPES.setAccessible(true);
			// make non final
			Field modifiers = Field.class.getDeclaredField("modifiers");
			modifiers.setAccessible(true);
			modifiers.setInt(TYPES, TYPES.getModifiers() & ~Modifier.FINAL);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}

