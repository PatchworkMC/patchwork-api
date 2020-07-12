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

import javax.annotation.Nonnull;

import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeBlockState;
import net.minecraftforge.common.extensions.IForgeItem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

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
	public static boolean canHarvestBlock(@Nonnull BlockState state, @Nonnull PlayerEntity player, @Nonnull BlockView world, @Nonnull BlockPos pos) {
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
}
