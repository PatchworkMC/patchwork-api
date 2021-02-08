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

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CollisionView;

@Mixin(Block.class)
public class MixinBlock implements IForgeBlock {
	protected Random RANDOM = new Random();

	@Shadow
	@Final
	private float slipperiness;

	@Unique
	private Set<Identifier> cachedTags;
	@Unique
	private int tagVersion;

	@Override
	public float getSlipperiness(BlockState state, CollisionView world, BlockPos pos, Entity entity) {
		return slipperiness;
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
		if (cachedTags == null || tagVersion != BlockTagsAccessor.getLatestVersion()) {
			this.cachedTags = new HashSet<>();

			for (final Map.Entry<Identifier, Tag<Block>> entry : BlockTags.getTagGroup().getEntries().entrySet()) {
				if (entry.getValue().contains((Block) (Object) this)) {
					cachedTags.add(entry.getKey());
				}
			}

			this.tagVersion = BlockTagsAccessor.getLatestVersion();
		}

		return this.cachedTags;
	}

	@Override
	public Random getRandom() {
		return RANDOM;
	}
}
