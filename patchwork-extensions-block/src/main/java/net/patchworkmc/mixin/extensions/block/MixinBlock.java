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

import java.util.Set;

import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

@Mixin(Block.class)
public class MixinBlock implements IForgeBlock {
	@Unique
	private Set<Identifier> cachedTags;
	@Unique
	private int tagVersion;
	@Unique
	private final net.minecraftforge.common.util.ReverseTagWrapper<Block> reverseTags = new net.minecraftforge.common.util.ReverseTagWrapper<>((Block) (Object) this, BlockTags::getTagGroup);

	@Override
	public float getSlipperiness(BlockState state, WorldView world, BlockPos pos, @Nullable Entity entity) {
		return 0;
	}

	@Override
	public ToolType getHarvestTool(BlockState state) {
		throw new UnsupportedOperationException("Harvest levels not yet implemented"); // TODO implement getHarvestLevel
	}

	@Override
	public int getHarvestLevel(BlockState state) {
		throw new UnsupportedOperationException("Harvest levels not yet implemented"); // TODO implement getHarvestLevel, really sucks for vanilla blocks so i'm putting it off
	}

	@Override
	public Set<Identifier> getTags() {
		return reverseTags.getTagNames();
	}
}
