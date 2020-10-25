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

package net.minecraftforge.common.extensions;

import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.patchworkmc.impl.extensions.blockentity.PatchworkBlockEntity;

public interface IForgeTileEntity extends ICapabilitySerializable<CompoundTag>, PatchworkBlockEntity {
	/**
	 * Sometimes default render bounding box: infinite in scope. Used to control rendering on {@link TileEntitySpecialRenderer}.
	 */
	Box INFINITE_EXTENT_AABB = new Box(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

	default BlockEntity getTileEntity() {
		return (BlockEntity) this;
	}

	@Override
	default void deserializeNBT(CompoundTag nbt) {
		getTileEntity().fromTag(nbt);
	}

	@Override
	default CompoundTag serializeNBT() {
		CompoundTag ret = new CompoundTag();
		getTileEntity().toTag(ret);
		return ret;
	}

	/**
	 * Called when you receive a TileEntityData packet for the location this
	 * TileEntity is currently in. On the client, the NetworkManager will always
	 * be the remote server. On the server, it will be whomever is responsible for
	 * sending the packet.
	 *
	 * @param net The NetworkManager the packet originated from
	 * @param pkt The data packet
	 */
	default void onDataPacket(ClientConnection net, BlockEntityUpdateS2CPacket pkt) {
	}

	/**
	 * Called when the chunk's TE update tag, gotten from {@link #getUpdateTag()}, is received on the client.
	 *
	 * <p>Used to handle this tag in a special way. By default this simply calls {@link #readFromNBT(NBTTagCompound)}.
	 *
	 * @param tag The {@link NBTTagCompound} sent from {@link #getUpdateTag()}
	 */
	default void handleUpdateTag(CompoundTag tag) {
		getTileEntity().fromTag(tag);
	}

	/**
	 * Gets a {@link NBTTagCompound} that can be used to store custom data for this tile entity.
	 * It will be written, and read from disc, so it persists over world saves.
	 *
	 * @return A compound tag for custom data
	 */
	CompoundTag getTileData();

	default void onChunkUnloaded() {
	}

	/**
	 * Called when this is first added to the world (by {@link World#addTileEntity(TileEntity)}).
	 * Override instead of adding {@code if (firstTick)} stuff in update.
	 */
	default void onLoad() {
		requestModelDataUpdate();
	}

	/**
	 * Return an {@link AxisAlignedBB} that controls the visible scope of a {@link TileEntitySpecialRenderer} associated with this {@link TileEntity}
	 * Defaults to the collision bounding box {@link Block#getCollisionBoundingBoxFromPool(World, int, int, int)} associated with the block
	 * at this location.
	 *
	 * @return an appropriately size {@link AxisAlignedBB} for the {@link TileEntity}
	 */
	@Environment(EnvType.CLIENT)
	default Box getRenderBoundingBox() {
		Box bb = INFINITE_EXTENT_AABB;
		BlockState state = getTileEntity().getCachedState();
		Block block = state.getBlock();
		BlockPos pos = getTileEntity().getPos();

		if (block == Blocks.ENCHANTING_TABLE) {
			bb = new Box(pos, pos.add(1, 1, 1));
		} else if (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST) {
			bb = new Box(pos.add(-1, 0, -1), pos.add(2, 2, 2));
		} else if (block == Blocks.STRUCTURE_BLOCK) {
			bb = INFINITE_EXTENT_AABB;
		} else if (block != null && block != Blocks.BEACON) {
			Box cbb = null;

			try {
				cbb = state.getCollisionShape(getTileEntity().getWorld(), pos).getBoundingBox().offset(pos);
			} catch (Exception e) {
				// We have to capture any exceptions that may occur here because BUKKIT servers like to send
				// the tile entity data BEFORE the chunk data, you know, the OPPOSITE of what vanilla does!
				// So we can not GUARANTEE that the world state is the real state for the block...
				// So, once again in the long line of US having to accommodate BUKKIT breaking things,
				// here it is, assume that the TE is only 1 cubic block. Problem with this is that it may
				// cause the TileEntity renderer to error further down the line! But alas, nothing we can do.
				cbb = new Box(pos.add(-1, 0, -1), pos.add(1, 1, 1));
			}

			if (cbb != null) {
				bb = cbb;
			}
		}

		return bb;
	}

	/**
	 * Checks if this tile entity knows how to render its 'breaking' overlay effect.
	 * If this returns true, The TileEntitySpecialRenderer will be called again with break progress set.
	 *
	 * @return True to re-render tile with breaking effect.
	 */
	default boolean canRenderBreaking() {
		Block block = getTileEntity().getCachedState().getBlock();
		return (block instanceof ChestBlock
				|| block instanceof EnderChestBlock
				|| block instanceof AbstractSignBlock
				|| block instanceof SkullBlock);
	}

	/**
	 * TODO: Deprecated, in Patchwork API 1.14.4, vanilla 1.15 and above,
	 * this is never called. See {@link net.minecraftforge.client.model.animation.TileEntityRendererFast}.
	 *
	 * <p>If the TileEntitySpecialRenderer associated with this TileEntity can be batched in with another renderers, and won't access the GL state.
	 * If TileEntity returns true, then TESR should have the same functionality as (and probably extend) the FastTESR class.
	 */
	@Deprecated
	default boolean hasFastRenderer() {
		return false;
	}
}
