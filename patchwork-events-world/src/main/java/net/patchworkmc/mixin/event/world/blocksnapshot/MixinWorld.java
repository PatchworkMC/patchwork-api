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

package net.patchworkmc.mixin.event.world.blocksnapshot;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import javax.annotation.Nullable;

import org.objectweb.asm.Opcodes;
import net.minecraftforge.common.util.BlockSnapshot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import net.patchworkmc.impl.event.world.BlockSnapshotStateAccess;
import net.patchworkmc.impl.event.world.MarkAndNotifyBlockAccess;
import net.patchworkmc.impl.event.world.Signatures;

@Mixin(World.class)
public abstract class MixinWorld implements MarkAndNotifyBlockAccess, BlockSnapshotStateAccess, IWorld {
	@Shadow
	public abstract void checkBlockRerender(BlockPos pos, BlockState old, BlockState updated);

	@Shadow
	public abstract void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags);

	@Shadow
	@Final
	public boolean isClient;

	@Shadow
	public abstract void updateHorizontalAdjacent(BlockPos pos, Block block);

	@Shadow
	public abstract void onBlockChanged(BlockPos pos, BlockState oldBlock, BlockState newBlock);

	@Shadow
	@Nullable
	public abstract MinecraftServer getServer();

	// TODO before merge: give some way to make this accessible in Fabric mods
	public boolean restoringBlockSnapshots = false; // TODO: impl this
	public boolean captureBlockSnapshots = false;
	public ArrayList<BlockSnapshot> capturedBlockSnapshots = new java.util.ArrayList<>();

	@Unique
	private BlockSnapshot snapshot = null;

	@Inject(method = Signatures.setBlockState_WITH_FLAGS, at = @At(value = "INVOKE_ASSIGN",
				target = "Lnet/minecraft/world/chunk/WorldChunk;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)Lnet/minecraft/block/BlockState;"))
	private void captureBlockSnapshot(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<Boolean> cir) {
		if (captureBlockSnapshots && !this.isClient) {
			// We know we're on the server, so let's make sure we're on thread.
			if (!this.getServer().isOnThread()) {
				// My approach is that if it becomes a problem, we can fix it. - Glitch
				throw new ConcurrentModificationException("Someone is calling setBlockState off-thread!");
			} else if (this.snapshot != null) {
				throw new IllegalStateException("BlockSnapshot is not clean!");
			}

			this.snapshot = BlockSnapshot.getBlockSnapshot(this, pos, flags);
		}
	}

	/**
	 * Despite using the TAIL injection, this injection point targets:
	 * <pre>
	 *      {@code
	 *
	 *      WorldChunk worldChunk = this.getWorldChunk(pos);
	 *      Block block = state.getBlock();
	 *      BlockState blockState = worldChunk.setBlockState(pos, state, (flags & 64) != 0);
	 *      if (blockState == null) {
	 *          // HERE
	 *          return false;
	 *      } else {
	 *         BlockState blockState2 = this.getBlockState(pos);
	 *         ...
	 *      }}
	 * </pre>
	 * Javac moves the return to the very end of the method, hence the odd injection point.
	 */
	@Inject(method = Signatures.setBlockState_WITH_FLAGS,
			at = @At(value = "TAIL"))
	private void nukeBlockSnapshot(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<Boolean> cir) {
		if (this.snapshot != null) {
			this.capturedBlockSnapshots.remove(snapshot);
		}

		// Mixin applies injections in the order they appear here, so the state will be cleaned up by the RETURN inject below.
	}

	/**
	 * This injection point targets:
	 * <pre>
	 *      {@code
	 *      if (blockState2 != blockState && (blockState2.getOpacity(this, pos) != (...)) {
	 *          this.profiler.push("queueCheckLight");
	 *          this.getChunkManager().getLightingProvider().checkBlock(pos);
	 *          this.profiler.pop();
	 *      }
	 *      // HERE
	 *      if (blockState2 == state) {
	 *          if (blockState != blockState2) {
	 *              this.checkBlockRerender(pos, blockState, blockState2);
	 *          }
	 *          ...
	 *      }
	 *      }
	 * </pre>
	 * The bytecode for the first if statement jumps to label 13 if the check fails,
	 * so we need to fit in before the 2nd if instruction but after the label 13 marker.
	 */
	@Inject(method = Signatures.setBlockState_WITH_FLAGS,
			at = @At(value = "JUMP", opcode = Opcodes.IF_ACMPNE, ordinal = 1), cancellable = true)
	private void shortCircuitIfCapturingSnapshots(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<Boolean> cir) {
		if (snapshot != null) {
			snapshot = null; // Patchwork: Clean state
			cir.setReturnValue(true); // Forge: Don't notify clients or update physics while capturing blockstates
		}
	}

	// Note that this runs for *every* return, minus our injection above.
	@Inject(method = Signatures.setBlockState_WITH_FLAGS, at = @At("RETURN"))
	private void cleanupState(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<Boolean> cir) {
		if (snapshot != null) {
			snapshot = null;
		}
	}

	// Exact copy of the latter half of setBlockState except where noted
	@Override
	public void markAndNotifyBlock(BlockPos pos, @Nullable WorldChunk chunk, BlockState blockstate, BlockState newState, int flags) {
		Block block = newState.getBlock();
		BlockState blockstate1 = getBlockState(pos);

		if (blockstate1 == newState) {
			if (blockstate != blockstate1) {
				this.checkBlockRerender(pos, blockstate, blockstate1);
			}

			// Forge: allow WorldChunk to be null
			if ((flags & 2) != 0 && (!this.isClient || (flags & 4) == 0) && (this.isClient || chunk == null || chunk.getLevelType() != null && chunk.getLevelType().isAfter(ChunkHolder.LevelType.TICKING))) {
				this.updateListeners(pos, blockstate, newState, flags);
			}

			if (!this.isClient && (flags & 1) != 0) {
				this.updateNeighbors(pos, blockstate.getBlock());

				if (newState.hasComparatorOutput()) {
					this.updateHorizontalAdjacent(pos, block);
				}
			}

			if ((flags & 16) == 0) {
				int i = flags & -2;
				blockstate.method_11637(this, pos, i);
				newState.updateNeighborStates(this, pos, i);
				newState.method_11637(this, pos, i);
			}

			this.onBlockChanged(pos, blockstate, blockstate1);
		}
	}
}
