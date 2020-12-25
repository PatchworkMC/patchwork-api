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

package net.patchworkmc.api.redirects.block;

import net.minecraft.block.Block;
import net.minecraft.sound.BlockSoundGroup;

import net.fabricmc.fabric.api.block.FabricBlockSettings;

public class PatchworkBlockSettings {
	public static Block.Settings sounds(Block.Settings settings, BlockSoundGroup sounds) {
		return FabricBlockSettings.copyOf(settings).sounds(sounds).build();
	}

	public static Block.Settings lightLevel(Block.Settings settings, int level) {
		return FabricBlockSettings.copyOf(settings).lightLevel(level).build();
	}

	public static Block.Settings breakInstantly(Block.Settings settings) {
		return settings.strength(0.0F, 0.0F);
	}

	public static Block.Settings strength(Block.Settings settings, float strength) {
		return settings.strength(strength, strength);
	}

	public static Block.Settings ticksRandomly(Block.Settings settings) {
		return FabricBlockSettings.copyOf(settings).ticksRandomly().build();
	}

	public static Block.Settings hasDynamicBounds(Block.Settings settings) {
		return FabricBlockSettings.copyOf(settings).dynamicBounds().build();
	}

	public static Block.Settings dropsNothing(Block.Settings settings) {
		return FabricBlockSettings.copyOf(settings).dropsNothing().build();
	}
}
