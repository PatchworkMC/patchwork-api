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

import java.util.Map;

import net.minecraftforge.common.extensions.IForgeBlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(WorldChunk.class)
public abstract class MixinWorldChunk {
	@Shadow
	@Final
	@Nullable
	public static ChunkSection EMPTY_SECTION;

	@Shadow
	@Final
	private ChunkSection[] sections;

	@Shadow
	@Final
	private Map<Heightmap.Type, Heightmap> heightmaps;

	@Shadow
	@Final
	private World world;

	@Shadow
	private volatile boolean shouldSave;

	@Shadow
	@Nullable
	public abstract BlockEntity getBlockEntity(BlockPos pos, WorldChunk.CreationType creationType);

	/**
	 * @author glitch
	 * @reason these injects are a pain to write and hasn't broken something yet :tm:
	 */
	@Overwrite
	@Nullable
	public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
		int i = pos.getX() & 15;
		int j = pos.getY();
		int k = pos.getZ() & 15;
		ChunkSection chunkSection = this.sections[j >> 4];

		if (chunkSection == EMPTY_SECTION) {
			if (state.isAir()) {
				return null;
			}

			chunkSection = new ChunkSection(j >> 4 << 4);
			this.sections[j >> 4] = chunkSection;
		}

		boolean bl = chunkSection.isEmpty();
		BlockState blockState = chunkSection.setBlockState(i, j & 15, k, state);

		if (blockState == state) {
			return null;
		} else {
			Block block = state.getBlock();
			Block block2 = blockState.getBlock();
			((Heightmap) this.heightmaps.get(Heightmap.Type.MOTION_BLOCKING)).trackUpdate(i, j, k, state);
			((Heightmap) this.heightmaps.get(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES)).trackUpdate(i, j, k, state);
			((Heightmap) this.heightmaps.get(Heightmap.Type.OCEAN_FLOOR)).trackUpdate(i, j, k, state);
			((Heightmap) this.heightmaps.get(Heightmap.Type.WORLD_SURFACE)).trackUpdate(i, j, k, state);
			boolean bl2 = chunkSection.isEmpty();

			if (bl != bl2) {
				this.world.getChunkManager().getLightingProvider().setSectionStatus(pos, bl2);
			}

			if (!this.world.isClient) {
				blockState.onStateReplaced(this.world, pos, state, moved);
			} else if (block2 != block || !((IForgeBlockState) state).hasTileEntity() && ((IForgeBlockState) blockState).hasTileEntity()) {
				this.world.removeBlockEntity(pos);
			}

			if (!chunkSection.getBlockState(i, j & 15, k).isOf(block)) {
				return null;
			} else {
				BlockEntity blockEntity2;

				if (block2 instanceof BlockEntityProvider) {
					blockEntity2 = this.getBlockEntity(pos, WorldChunk.CreationType.CHECK);

					if (blockEntity2 != null) {
						blockEntity2.resetBlock();
					}
				}

				if (!this.world.isClient) {
					state.onBlockAdded(this.world, pos, blockState, moved);
				}

				if (block instanceof BlockEntityProvider) {
					blockEntity2 = this.getBlockEntity(pos, WorldChunk.CreationType.CHECK);

					if (blockEntity2 == null) {
						blockEntity2 = ((BlockEntityProvider) block).createBlockEntity(this.world);
						this.world.setBlockEntity(pos, blockEntity2);
					} else {
						blockEntity2.resetBlock();
					}
				}

				this.shouldSave = true;
				return blockState;
			}
		}
	}
}

