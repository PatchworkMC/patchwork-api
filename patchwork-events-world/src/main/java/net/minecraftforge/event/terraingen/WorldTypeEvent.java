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

package net.minecraftforge.event.terraingen;

import net.minecraftforge.eventbus.api.Event;

import net.minecraft.world.level.LevelGeneratorType;

public class WorldTypeEvent extends Event {
	private final LevelGeneratorType worldType;

	public WorldTypeEvent(LevelGeneratorType worldType) {
		this.worldType = worldType;
	}

	public LevelGeneratorType getWorldType() {
		return worldType;
	}

	public static class BiomeSize extends WorldTypeEvent {
		private final int originalSize;
		private int newSize;

		public BiomeSize(LevelGeneratorType worldType, int original) {
			super(worldType);
			originalSize = original;
			setNewSize(original);
		}

		public int getOriginalSize() {
			return originalSize;
		}

		public int getNewSize() {
			return newSize;
		}

		public void setNewSize(int newSize) {
			this.newSize = newSize;
		}
	}
}
