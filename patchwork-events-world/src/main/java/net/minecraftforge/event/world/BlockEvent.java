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

import net.minecraftforge.eventbus.api.Event;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockEvent extends Event {
	private final IWorld world;
	private final BlockPos pos;
	private final BlockState state;

	// For EventBus
	public BlockEvent() {
		this(null, null, null);
	}

	public BlockEvent(IWorld world, BlockPos pos, BlockState state) {
		this.pos = pos;
		this.world = world;
		this.state = state;
	}

	public IWorld getWorld() {
		return world;
	}

	public BlockPos getPos() {
		return pos;
	}

	public BlockState getState() {
		return state;
	}

	/**
	 * This event is fired when an {@link net.minecraft.block.Block} is about to be broken by a player.
	 *
	 * <p>This event is cancellable.
	 * Cancelling this event will prevent the {@link net.minecraft.block.Block} from being broken.</p>
	 */
	public static class BreakEvent extends BlockEvent {
		/**
		 * Reference to the {@link PlayerEntity} who broke the block. If no player is available, use an {@link EntityFakePlayer}
		 */
		private final PlayerEntity player;
		private int exp;

		// For EventBus
		public BreakEvent() {
			this(null, null, null, null);
		}

		public BreakEvent(World world, BlockPos pos, BlockState state, PlayerEntity player) {
			super(world, pos, state);

			this.player = player;
			this.exp = 0;

			// TODO: BlockState#getExpDrop

			/*
			// Handle empty block or player unable to break block scenario
			if (state == null || !ForgeHooks.canHarvestBlock(state, player, world, pos)) {
				this.exp = 0;
			} else {
				int bonusLevel = EnchantmentHelper.getLevel(Enchantments.FORTUNE, player.getHeldItemMainhand());
				int silklevel = EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, player.getHeldItemMainhand());
				this.exp = state.getExpDrop(world, pos, bonusLevel, silklevel);
			}*/
		}

		public PlayerEntity getPlayer() {
			return player;
		}

		/**
		 * Gets the experience dropped by the block after the event has processed.
		 *
		 * @return The experience to drop or 0 if the event was cancelled
		 */
		public int getExpToDrop() {
			return this.isCanceled() ? 0 : exp;
		}

		/**
		 * Sets the amount of experience dropped by the block after the event has processed.
		 *
		 * @param exp 1 or higher to drop experience, else nothing will drop
		 */
		public void setExpToDrop(int exp) {
			this.exp = exp;
		}

		@Override
		public boolean isCancelable() {
			return true;
		}
	}
}
