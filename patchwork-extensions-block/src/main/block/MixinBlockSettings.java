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

package net.patchworkmc.mixin.extensions.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import net.minecraftforge.common.ToolType;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tools.FabricToolTags;

import net.patchworkmc.api.block.IPatchworkBlockSettings;

@Mixin(Block.Settings.class)
public class MixinBlockSettings implements IPatchworkBlockSettings {
	@Unique
	private Integer miningLevel;
	@Unique
	private Tag<Item> miningTool;

	public Block.Settings harvestLevel(int harvestLevel) {
		this.miningLevel = new Integer(harvestLevel);

		if (this.miningTool != null) {
			FabricBlockSettings fabric = FabricBlockSettings.copyOf((Block.Settings) (Object) this);
			return fabric.breakByTool(this.miningTool, harvestLevel).build();
		} else {
			return (Block.Settings) (Object) this;
		}
	}

	public Block.Settings harvestTool(ToolType harvestTool) {
		String name = harvestTool.getName();

		switch (name) {
		case "axe":
			this.miningTool = FabricToolTags.AXES;
			break;
		case "hoe":
			this.miningTool = FabricToolTags.HOES;
			break;
		case "pickaxe":
			this.miningTool = FabricToolTags.PICKAXES;
			break;
		case "shovel":
			this.miningTool = FabricToolTags.SHOVELS;
			break;
		case "sword":
			this.miningTool = FabricToolTags.SWORDS;
			break;
		}

		FabricBlockSettings fabric = FabricBlockSettings.copyOf((Block.Settings) (Object) this);

		if (this.miningLevel != null) {
			return fabric.breakByTool(this.miningTool, this.miningLevel.intValue()).build();
		} else {
			return fabric.breakByTool(this.miningTool).build();
		}
	}
}
