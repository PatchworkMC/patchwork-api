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

package net.patchworkmc.api.levelgenerators;

import java.util.Arrays;

import net.minecraft.world.level.LevelGeneratorType;

import net.patchworkmc.mixin.levelgenerators.AccessorLevelGeneratorType;

/**
 * Used by Patchwork to mark forge level generator types and implement the forge {@linkplain LevelGeneratorType} constructor. Added in the patching phase by patcher.
 */
public class PatchworkLevelGeneratorType extends LevelGeneratorType {
	public PatchworkLevelGeneratorType(String name) {
		super(getNextID(), name);
	}

	private static int getNextID() {
		LevelGeneratorType[] types = LevelGeneratorType.TYPES;

		for (int x = 0; x < types.length; x++) {
			if (types[x] == null) {
				return x;
			}
		}

		int old = types.length;
		AccessorLevelGeneratorType.patchwork$setTypes(Arrays.copyOf(types, old + 16));
		return old;
	}
}
