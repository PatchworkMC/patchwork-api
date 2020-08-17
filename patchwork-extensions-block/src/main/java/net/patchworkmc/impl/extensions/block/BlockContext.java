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

import net.minecraftforge.common.extensions.IForgeBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class BlockContext {
	private static final ThreadLocal<Object> hasBlockEntity_blockState = createContext();

	//////////////////////////////////////////////////////////////
	/// Context helper
	/// Pass parameters without using method args
	/// Thread safe, but DOES NOT support recursive calls
	/// It is the caller's responsibility to maintain the context
	//////////////////////////////////////////////////////////////
	private static final Object CLEAN_MARKER = new Object();

	public static ThreadLocal<Object> createContext() {
		return ThreadLocal.withInitial(() -> BlockContext.CLEAN_MARKER);
	}

	public static void setContext(ThreadLocal<Object> context, Object value) {
		Object oldValue = context.get();

		if (oldValue != CLEAN_MARKER) {
			throw new IllegalStateException("The context is not clean.");
		}

		context.set(value);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getContext(ThreadLocal<Object> context) {
		Object oldValue = context.get();

		if (oldValue == CLEAN_MARKER) {
			throw new IllegalStateException("The context is not set.");
		}

		return (T) context.get();
	}

	@SuppressWarnings("unchecked")
	public static <T> T getContextOr(ThreadLocal<Object> context, T defaultValue) {
		Object value = context.get();

		if (value == CLEAN_MARKER) {
			return defaultValue;
		} else {
			return (T) value;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T releaseContext(ThreadLocal<Object> context) {
		Object oldValue = context.get();

		if (oldValue == CLEAN_MARKER) {
			throw new IllegalStateException("The context is not set.");
		}

		context.remove();

		return (T) oldValue;
	}

	///////////////////////////////////////////////////////
	/// Block.hasBlockEntity()
	///////////////////////////////////////////////////////
	/**
	 * Called by mixin methods.
	 * @param blockState
	 * @return
	 */
	public static boolean hasBlockEntity(BlockState blockState) {
		setContext(hasBlockEntity_blockState, blockState);
		Block block = blockState.getBlock();
		boolean ret = block.hasBlockEntity();
		releaseContext(hasBlockEntity_blockState);
		return ret;
	}

	/**
	 * Called by vanilla Block Class, as a wrapper which redirects the call to Forge's BlockState sensitive version.
	 * @param forgeBlock
	 * @return
	 */
	public static boolean block_hasBlockEntity(IForgeBlock forgeBlock) {
		BlockState blockState = getContextOr(hasBlockEntity_blockState, forgeBlock.getBlock().getDefaultState());

		return forgeBlock.hasTileEntity(blockState);
	}

	/**
	 * Called by mixin methods. The return value is used to pass the vanilla Block.hasBlockEntity or block instanceof BlockEntityProvider.
	 * @param hasBlockEntity
	 * @return Blocks.CHEST (Always have a BlockEntity) if hasBlockEntity is true, otherwise Blocks.AIR (impossible to host BlockEntity)
	 */
	public static Block hasBlockEntityBlockMarker(boolean hasBlockEntity) {
		return hasBlockEntity ? Blocks.CHEST : Blocks.AIR;
	}

	public static Block hasBlockEntityBlockMarker(BlockState blockState) {
		return hasBlockEntityBlockMarker(hasBlockEntity(blockState));
	}
}
