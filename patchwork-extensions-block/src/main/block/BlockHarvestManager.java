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

package net.patchworkmc.impl.extensions.block;

import java.util.Stack;

import org.jetbrains.annotations.NotNull;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeBlockState;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.event.world.BlockEvent;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

public class BlockHarvestManager {
	private static final ThreadLocal<Stack<Integer>> expDrops = ThreadLocal.withInitial(Stack::new);

	private static void checkExpDropStack() {
		if (expDrops.get().isEmpty()) {
			throw new IllegalStateException("Patchwork's experience drop stack is not balanced!");
		}
	}

	public static void pushExpDropStack(int exp) {
		expDrops.get().push(exp);
	}

	public static int getLastExpDrop() {
		checkExpDropStack();
		return expDrops.get().lastElement();
	}

	public static int popExpDropStack() {
		checkExpDropStack();
		return expDrops.get().pop();
	}

	/**
	 * Called by Mixins and ForgeHooks.canHarvestBlock,
	 * Requires harvest levels.
	 */
	@SuppressWarnings("unused")
	public static boolean canHarvestBlock(@NotNull BlockState state, @NotNull PlayerEntity player, @NotNull BlockView world, @NotNull BlockPos pos) {
		// state = state.getActualState(world, pos);
		if (state.getMaterial().canBreakByHand()) {
			return true;
		}

		ItemStack stack = player.getMainHandStack();
		ToolType tool = null; // TODO: Unimplemented: ((IForgeBlockState) state).getHarvestTool();

		if (stack.isEmpty() || tool == null) {
			return player.isUsingEffectiveTool(state);
		}

		int toolLevel = ((IForgeItem) stack.getItem()).getHarvestLevel(stack, tool, player, state);

		if (toolLevel < 0) {
			return player.isUsingEffectiveTool(state);
		}

		return toolLevel >= ((IForgeBlockState) state).getHarvestLevel();
	}

	public static boolean isVanillaBlock(Block block) {
		if (block instanceof OreBlock || block instanceof RedstoneOreBlock) {
			return true;
		}

		if (block instanceof SpawnerBlock) {
			return true;
		}

		return false;
	}

	/**
	 * Called by Mixin and ForgeHooks.
	 * @return experience dropped, -1 = block breaking is cancelled.
	 */
	public static int onBlockBreakEvent(World world, GameMode gameMode, ServerPlayerEntity player, BlockPos pos) {
		// Logic from tryHarvestBlock for pre-canceling the event
		boolean preCancelEvent = false;

		ItemStack itemstack = player.getMainHandStack();

		if (!itemstack.isEmpty() && !itemstack.getItem().canMine(world.getBlockState(pos), world, pos, player)) {
			preCancelEvent = true;
		}

		// method_21701 => canMine
		// Isn't the function really canNotMine?

		if (player.isBlockBreakingRestricted(world, pos, gameMode)) {
			preCancelEvent = true;
		}

		// Tell client the block is gone immediately then process events
		if (world.getBlockEntity(pos) == null) {
			player.networkHandler.sendPacket(new BlockUpdateS2CPacket(EmptyBlockView.INSTANCE, pos));
		}

		// Post the block break event
		BlockState state = world.getBlockState(pos);
		BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, state, player);
		event.setCanceled(preCancelEvent);
		MinecraftForge.EVENT_BUS.post(event);

		// Handle if the event is canceled
		if (event.isCanceled()) {
			// Let the client know the block still exists
			player.networkHandler.sendPacket(new BlockUpdateS2CPacket(world, pos));

			// Update any block entity data for this block
			BlockEntity entity = world.getBlockEntity(pos);

			if (entity != null) {
				BlockEntityUpdateS2CPacket packet = entity.toUpdatePacket();

				if (packet != null) {
					player.networkHandler.sendPacket(packet);
				}
			}

			return -1; // Cancelled
		} else {
			return event.getExpToDrop();
		}
	}

	// TODO: Leaving this unfired is intentional. See: https://github.com/MinecraftForge/MinecraftForge/issues/5828
	@Deprecated
	public static float fireBlockHarvesting(DefaultedList<ItemStack> drops, World world, BlockPos pos, BlockState state, int fortune, float dropChance, boolean silkTouch, PlayerEntity player) {
		BlockEvent.HarvestDropsEvent event = new BlockEvent.HarvestDropsEvent(world, pos, state, fortune, dropChance, drops, player, silkTouch);
		MinecraftForge.EVENT_BUS.post(event);
		return event.getDropChance();
	}

	public static boolean onFarmlandTrample(World world, BlockPos pos, BlockState state, float fallDistance, Entity entity) {
		// TODO: In forge, the possibility of trampling is handled by IForgeEntity.canTrample
		// Maybe there's a good way to reconcile that to not break any Fabric mods trying to
		// manipulate crop trampling, but for now I just let the vanilla check do it's thing.
		BlockEvent.FarmlandTrampleEvent event = new BlockEvent.FarmlandTrampleEvent(world, pos, state, fallDistance, entity);
		MinecraftForge.EVENT_BUS.post(event);
		return !event.isCanceled();
	}
}
