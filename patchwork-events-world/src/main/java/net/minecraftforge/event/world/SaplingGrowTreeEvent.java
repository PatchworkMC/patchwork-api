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

package net.minecraftforge.event.world;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

/**
 * SaplingGrowTreeEvent is fired when a sapling grows into a tree.
 *
 * <p>This event is fired during sapling growth in
 * {@link SaplingBlock#generate(ServerWorld, BlockPos, BlockState, Random)}.
 *
 * <p>{@link #pos} contains the coordinates of the growing sapling.
 * {@link #rand} contains an instance of Random for use.
 *
 * <p>This event is not cancellable.
 *
 * <p>This event has a result.
 * This result determines if the sapling is allowed to grow.
 */
public class SaplingGrowTreeEvent extends WorldEvent {
	private final BlockPos pos;
	private final Random rand;

	public SaplingGrowTreeEvent(WorldAccess world, Random rand, BlockPos pos) {
		super(world);
		this.rand = rand;
		this.pos = pos;
	}

	public BlockPos getPos() {
		return pos;
	}

	public Random getRand() {
		return rand;
	}

	@Override
	public boolean hasResult() {
		return true;
	}
}
