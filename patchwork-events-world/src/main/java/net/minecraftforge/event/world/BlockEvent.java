/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Event;

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
	 * Event that is fired when an Block is about to be broken by a player
	 * Canceling this event will prevent the Block from being broken.
	 */
	public static class BreakEvent extends BlockEvent {
		/**
		 * Reference to the Player who broke the block. If no player is available, use an EntityFakePlayer
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
			/*if (state == null || !ForgeHooks.canHarvestBlock(state, player, world, pos)) // Handle empty block or player unable to break block scenario
			{
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
		 * Get the experience dropped by the block after the event has processed
		 *
		 * @return The experience to drop or 0 if the event was canceled
		 */
		public int getExpToDrop() {
			return this.isCanceled() ? 0 : exp;
		}

		/**
		 * Set the amount of experience dropped by the block after the event has processed
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
