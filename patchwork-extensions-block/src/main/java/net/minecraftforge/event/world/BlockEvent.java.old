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

import java.util.List;

import net.minecraftforge.common.extensions.IForgeBlockState;
import net.minecraftforge.eventbus.api.Event;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.patchworkmc.impl.extensions.block.BlockHarvestManager;

public class BlockEvent extends Event {
	private final WorldAccess world;
	private final BlockPos pos;
	private final BlockState state;

	public BlockEvent(WorldAccess world, BlockPos pos, BlockState state) {
		this.pos = pos;
		this.world = world;
		this.state = state;
	}

	public WorldAccess getWorld() {
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

		public BreakEvent(World world, BlockPos pos, BlockState state, PlayerEntity player) {
			super(world, pos, state);

			this.player = player;
			this.exp = 0;

			// Handle empty block or player unable to break block scenario
			if (state == null || !BlockHarvestManager.canHarvestBlock(state, player, world, pos)) {
				this.exp = 0;
			} else {
				int bonusLevel = EnchantmentHelper.getLevel(Enchantments.FORTUNE, player.getMainHandStack());
				int silklevel = EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, player.getMainHandStack());
				this.exp = ((IForgeBlockState) state).getExpDrop(world, pos, bonusLevel, silklevel);
			}
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

	/**
	 * Fired when a block is about to drop it's harvested items. The {@link #drops} array can be amended, as can the {@link #dropChance}.
	 * <strong>Note well:</strong> the {@link #harvester} player field is null in a variety of scenarios. Code expecting null.
	 *
	 * <p>The {@link #dropChance} is used to determine which items in this array will actually drop, compared to a random number. If you wish, you
	 * can pre-filter yourself, and set {@link #dropChance} to 1.0f to always drop the contents of the {@link #drops} array.</p>
	 *
	 * <p>{@link #isSilkTouching} is set if this is considered a silk touch harvesting operation, vs a normal harvesting operation. Act accordingly.</p>
	 */
	@Deprecated
	public static class HarvestDropsEvent extends BlockEvent {
		private final int fortuneLevel;
		private final DefaultedList<ItemStack> drops;
		private final boolean isSilkTouching;
		private final PlayerEntity harvester; // May be null for non-player harvesting such as explosions or machines
		private float dropChance; // Change to e.g. 1.0f, if you manipulate the list and want to guarantee it always drops

		public HarvestDropsEvent(World world, BlockPos pos, BlockState state, int fortuneLevel, float dropChance, DefaultedList<ItemStack> drops, PlayerEntity harvester, boolean isSilkTouching) {
			super(world, pos, state);
			this.fortuneLevel = fortuneLevel;
			this.setDropChance(dropChance);
			this.drops = drops;
			this.isSilkTouching = isSilkTouching;
			this.harvester = harvester;
		}

		public int getFortuneLevel() {
			return fortuneLevel;
		}

		public List<ItemStack> getDrops() {
			return drops;
		}

		public boolean isSilkTouching() {
			return isSilkTouching;
		}

		public float getDropChance() {
			return dropChance;
		}

		public void setDropChance(float dropChance) {
			this.dropChance = dropChance;
		}

		public PlayerEntity getHarvester() {
			return harvester;
		}
	}

	/**
	 * Fired when when farmland gets trampled
	 * This event is cancellable.
	 */
	public static class FarmlandTrampleEvent extends BlockEvent {
		private final Entity entity;
		private final float fallDistance;

		public FarmlandTrampleEvent(World world, BlockPos pos, BlockState state, float fallDistance, Entity entity) {
			super(world, pos, state);
			this.entity = entity;
			this.fallDistance = fallDistance;
		}

		public Entity getEntity() {
			return entity;
		}

		public float getFallDistance() {
			return fallDistance;
		}

		@Override
		public boolean isCancelable() {
			return true;
		}
	}
}
