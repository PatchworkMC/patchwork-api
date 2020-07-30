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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import net.minecraftforge.common.extensions.IForgeTileEntity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;

/**
 * TODO: This Mixin can be less hacky if we can talk to the Fabric API team.
 *
 * <p>This Mixin implements {@link IForgeTileEntity#handleUpdateTag(CompoundTag)}
 * and {@link IForgeTileEntity#onDataPacket(net.minecraft.network.ClientConnection, BlockEntityUpdateS2CPacket)}.
 */
@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {
	///////////////////////////////////////////////////////////////
	/// onChunkData -> IForgeTileEntity.handleUpdateTag
	///////////////////////////////////////////////////////////////
	@Unique
	private static final ThreadLocal<CompoundTag> onChunkData_BETag = ThreadLocal.withInitial(() -> null);
	@Unique
	private static final String ClientWorld_getBlockEntity = "net/minecraft/client/world/ClientWorld.getBlockEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/entity/BlockEntity;";

	@Inject(method = "onChunkData", locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(value = "INVOKE", shift = Shift.BEFORE, target = ClientWorld_getBlockEntity))
	private void onChunkData_CollectBETag(ChunkDataS2CPacket packet, CallbackInfo ci,
			@SuppressWarnings("rawtypes") Iterator blockEntityTagListIterator, CompoundTag blockEntityTag, BlockPos blockPos) {
		if (onBlockEntityUpdate_BlockEntity.get() != null) {
			throw new IllegalStateException("State of ClientPlayNetworkHandler.onChunkData() is not clean, incompatible Mixins might be the cause!");
		}

		onChunkData_BETag.set(blockEntityTag);
	}

	@Redirect(method = "onChunkData", at = @At(value = "INVOKE", target = ClientWorld_getBlockEntity))
	private BlockEntity onChunkData_world_getBlockEntity(ClientWorld clientWorld, BlockPos blockPos) {
		CompoundTag blockEntityTag = onChunkData_BETag.get();
		onChunkData_BETag.remove();

		ClientPlayNetworkHandler me = (ClientPlayNetworkHandler) (Object) this;
		BlockEntity blockEntity = me.getWorld().getBlockEntity(blockPos);

		if (blockEntity != null && !(blockEntity instanceof BlockEntityClientSerializable)) {
			// Non-Fabric BlockEntity, redirect to IForgeTileEntity.handleUpdateTag,
			// which then calls the vanilla BlockEntity.fromTag method.
			((IForgeTileEntity) blockEntity).handleUpdateTag(blockEntityTag);
			return null; // Skip Fabric's patch
		} else {
			return blockEntity; // Fabric BlockEntity, let Fabric API process it.
		}
	}

	///////////////////////////////////////////////////////////////
	/// onBlockEntityUpdate -> IForgeTileEntity.onDataPacket
	///////////////////////////////////////////////////////////////
	@Unique
	private static final ThreadLocal<BlockEntity> onBlockEntityUpdate_BlockEntity = ThreadLocal.withInitial(() -> null);

	@Inject(method = "onBlockEntityUpdate", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 0, shift = Shift.AFTER,
			target = "net/minecraft/network/packet/s2c/play/BlockEntityUpdateS2CPacket.getBlockEntityType()I"))
	private void onBlockEntityUpdate_getBlockEntityType(BlockEntityUpdateS2CPacket packet, CallbackInfo ci, BlockEntity blockEntity) {
		if (onBlockEntityUpdate_BlockEntity.get() != null) {
			throw new IllegalStateException("State of ClientPlayNetworkHandler.onBlockEntityUpdate() is not clean, incompatible Mixins might be the cause!");
		}

		onBlockEntityUpdate_BlockEntity.set(blockEntity);
	}

	@Inject(method = "onBlockEntityUpdate", at = @At(value = "INVOKE", ordinal = 0, shift = Shift.BEFORE,
			target = "net/minecraft/block/entity/BlockEntity.fromTag(Lnet/minecraft/nbt/CompoundTag;)V"))
	private void onBlockEntityUpdate_fromTag(CallbackInfo ci) {
		onBlockEntityUpdate_BlockEntity.remove();
	}

	@Inject(method = "onBlockEntityUpdate", at = @At(value = "RETURN"))
	private void onBlockEntityUpdate_Return(BlockEntityUpdateS2CPacket packet, CallbackInfo ci) {
		BlockEntity blockEntity = onBlockEntityUpdate_BlockEntity.get();

		if (blockEntity != null && !(blockEntity instanceof BlockEntityClientSerializable)) {
			onBlockEntityUpdate_BlockEntity.remove();
			ClientPlayNetworkHandler me = (ClientPlayNetworkHandler) (Object) this;
			((IForgeTileEntity) blockEntity).onDataPacket(me.getConnection(), packet);
		}
	}
}
