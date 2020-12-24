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

package net.patchworkmc.mixin.extensions.blockentity;

import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import net.minecraftforge.common.extensions.IForgeTileEntity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

/**
 * This Mixin implements {@link IForgeTileEntity#onLoad()} and {@link IForgeTileEntity#onChunkUnloaded()}.
 */
@Mixin(World.class)
public class MixinWorld {
	/////////////////////////////////////
	/// addBlockEntity()
	/////////////////////////////////////
	@Inject(method = "addBlockEntity", at = @At("HEAD"))
	private void onAddBlockEntity(BlockEntity blockEntity, CallbackInfoReturnable<Boolean> ci) {
		World me = (World) (Object) this;

		if (blockEntity.getWorld() != me) {
			// Forge - set the world early as vanilla doesn't set it until next tick
			blockEntity.setLocation(me);
		}
	}

	@Shadow
	@Final
	protected List<BlockEntity> pendingBlockEntities;

	@Inject(method = "addBlockEntity", cancellable = true,
			at = @At(value = "INVOKE", ordinal = 0, shift = Shift.AFTER, remap = false,
			target = "org/apache/logging/log4j/Logger.error(Ljava/lang/String;[Lorg/apache/logging/log4j/util/Supplier;)V"))
	private void onBlockEntityAdding(BlockEntity blockEntity, CallbackInfoReturnable<Boolean> cir) {
		// Forge: wait to add new TE if we're currently processing existing ones
		cir.setReturnValue(pendingBlockEntities.add(blockEntity));
	}

	@Inject(method = "addBlockEntity", at = @At(value = "FIELD", ordinal = 0, shift = Shift.BEFORE,
			target = "net/minecraft/world/World.isClient:Z", opcode = Opcodes.GETFIELD))
	private void onBlockEntityAdded(BlockEntity blockEntity, CallbackInfoReturnable<Boolean> cir) {
		// We cannot use Fabric events because Forge's onLoad() is also called on the client side.
		((IForgeTileEntity) blockEntity).onLoad();
	}

	/////////////////////////////////////
	/// tickBlockEntities()
	/////////////////////////////////////
	@Shadow
	protected boolean iteratingTickingBlockEntities;

	@Inject(method = "tickBlockEntities", at = @At(value = "INVOKE", ordinal = 0, shift = Shift.AFTER,
			target = "net/minecraft/util/profiler/Profiler.push(Ljava/lang/String;)V"))
	private void onTickBlockEntitiesStart(CallbackInfo ci) {
		// Forge: Move above remove to prevent CocurrentModificationException
		iteratingTickingBlockEntities = true;
	}

	@Shadow
	@Final
	protected List<BlockEntity> unloadedBlockEntities;

	@Inject(method = "tickBlockEntities", at = @At(value = "INVOKE", ordinal = 0, shift = Shift.BEFORE,
			target = "java/util/List.removeAll(Ljava/util/Collection;)Z"))
	private void onBlockEntitiesRemoved(CallbackInfo ci) {
		for (BlockEntity blockEntity: unloadedBlockEntities) {
			((IForgeTileEntity) blockEntity).onChunkUnloaded();
		}
	}

	@Unique
	private static final ThreadLocal<BlockEntity> onBlockEntitiesRemoved_BlockEntity = ThreadLocal.withInitial(() -> null);
	@Unique
	private static final String WorldChunk_removeBlockEntity = "net/minecraft/world/chunk/WorldChunk.removeBlockEntity(Lnet/minecraft/util/math/BlockPos;)V";

	@Inject(method = "tickBlockEntities", locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(value = "INVOKE", ordinal = 0, shift = Shift.BEFORE, target = WorldChunk_removeBlockEntity))
	private void onBlockEntitiesRemoved_CaptureVars(CallbackInfo ci,
			Profiler profiler, @SuppressWarnings("rawtypes") Iterator iterator, BlockEntity blockEntity) {
		if (onBlockEntitiesRemoved_BlockEntity.get() != null) {
			throw new IllegalStateException("State of World.onBlockEntitiesRemoved() is not clean, incompatible Mixins might be the cause!");
		}

		onBlockEntitiesRemoved_BlockEntity.set(blockEntity);
	}

	@Redirect(method = "tickBlockEntities", at = @At(value = "INVOKE", ordinal = 0, target = WorldChunk_removeBlockEntity))
	private void onBlockEntitiesRemoved(WorldChunk chunk, BlockPos pos) {
		BlockEntity blockEntity = onBlockEntitiesRemoved_BlockEntity.get();
		onBlockEntitiesRemoved_BlockEntity.remove();

		//Forge: Bugfix: If we set the tile entity it immediately sets it in the chunk, so we could be desyned
		if (chunk.getBlockEntity(pos, WorldChunk.CreationType.CHECK) == blockEntity) {
			chunk.removeBlockEntity(pos);
		}
	}

	/////////////////////////////////////
	/// setBlockEntity()
	/////////////////////////////////////
	@ModifyVariable(method = "setBlockEntity", at = @At("HEAD"))
	private BlockPos makeBlockPosImmutable(BlockPos pos) {
		// Forge - prevent mutable BlockPos leaks
		return pos.toImmutable();
	}

	@Inject(method = "setBlockEntity", at = @At(value = "INVOKE", ordinal = 0, shift = Shift.BEFORE,
			target = "java/util/List.iterator()Ljava/util/Iterator;"))
	private void setWorldEarly(BlockPos pos, BlockEntity blockEntity, CallbackInfo ci) {
		World me = (World) (Object) this;

		if (blockEntity.getWorld() != me) {
			// Forge - set the world early as vanilla doesn't set it until next tick
			blockEntity.setLocation(me);
		}
	}

	@Redirect(method = "setBlockEntity", at = @At(value = "INVOKE", ordinal = 0,
			target = "net/minecraft/world/chunk/WorldChunk.setBlockEntity(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;)V"))
	private void checkChunkAndSetBlockEntity(WorldChunk chunk, BlockPos pos, BlockEntity blockEntity) {
		// Forge adds this to prevent a null-pointer exception
		if (chunk != null) {
			chunk.setBlockEntity(pos, blockEntity);
		}
	}

	/////////////////////////////////////
	/// removeBlockEntity()
	/////////////////////////////////////
	@Inject(method = "removeBlockEntity", locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(value = "INVOKE", ordinal = 0, shift = Shift.AFTER,
			target = "java/util/List.remove(Ljava/lang/Object;)Z"))
	private void removeTickableBlockEntity(BlockPos pos, CallbackInfo ci, BlockEntity blockEntity) {
		if (!(blockEntity instanceof Tickable)) {
			//Forge: If they are not tickable they wont be removed in the update loop.
			World me = (World) (Object) this;
			me.blockEntities.remove(blockEntity);
		}
	}
}
