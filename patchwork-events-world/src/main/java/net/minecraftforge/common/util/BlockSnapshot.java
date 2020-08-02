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

package net.minecraftforge.common.util;

import java.lang.ref.WeakReference;
import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraftforge.fml.server.ServerLifecycleHooks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

/**
 * Represents a captured snapshot of a block which will not change
 * automatically.
 *
 * <p>Unlike Block, which only one object can exist per coordinate, BlockSnapshot
 * can exist multiple times for any given Block.
 */
public class BlockSnapshot {
	private static final Identifier AIR = new Identifier("air");
	private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("forge.debugBlockSnapshot", "false"));

	private final BlockPos pos;
	private final int dimId;
	@Nullable
	private final CompoundTag nbt;
	private final Identifier registryName;
	private final int meta = 0; // Forge: TODO BlockSnapshot needs a total refactor for the absence of metadata
	@Nullable
	private BlockState replacedBlock;
	private int flag;
	@Nullable
	private WeakReference<IWorld> world;

	public BlockSnapshot(IWorld world, BlockPos pos, BlockState state) {
		this(world, pos, state, getTileNBT(world.getBlockEntity(pos)));
	}

	public BlockSnapshot(IWorld world, BlockPos pos, BlockState state, @Nullable CompoundTag nbt) {
		this.setWorld(world);
		this.dimId = world.getDimension().getType().getRawId();
		this.pos = pos.toImmutable();
		this.setReplacedBlock(state);
		// Patchwork: get the name from the vanilla registry instead of from the IForgeRegistryEntry.
		// it's very doubtful this will break anything, so this follows coderbot's golden rule.
		this.registryName = Registry.BLOCK.getId(state.getBlock());

		// DefaultedRegistry; missing entries return air
		if (registryName.equals(AIR) && state.getBlock() != Blocks.AIR) {
			throw new IllegalArgumentException("Tried to capture a BlockState of a block that isn't registered?");
		}

		this.setFlag(3);
		this.nbt = nbt;

		if (DEBUG) {
			System.out.printf("Created BlockSnapshot - [World: %s ][Location: %d,%d,%d ][Block: %s ][Meta: %d ]", world.getLevelProperties().getLevelName(), pos.getX(), pos.getY(), pos.getZ(), getRegistryName(), getMeta());
		}
	}

	public BlockSnapshot(IWorld world, BlockPos pos, BlockState state, int flag) {
		this(world, pos, state);
		this.setFlag(flag);
	}

	/**
	 * Raw constructor designed for serialization usages.
	 */
	public BlockSnapshot(int dimension, BlockPos pos, Identifier registryName, int meta, int flag, @Nullable CompoundTag nbt) {
		this.dimId = dimension;
		this.pos = pos.toImmutable();
		this.setFlag(flag);
		this.registryName = registryName;
		this.nbt = nbt;
	}

	public static BlockSnapshot getBlockSnapshot(IWorld world, BlockPos pos) {
		return new BlockSnapshot(world, pos, world.getBlockState(pos));
	}

	public static BlockSnapshot getBlockSnapshot(IWorld world, BlockPos pos, int flag) {
		return new BlockSnapshot(world, pos, world.getBlockState(pos), flag);
	}

	public static BlockSnapshot readFromNBT(CompoundTag tag) {
		return new BlockSnapshot(
			tag.getInt("dimension"),
			new BlockPos(tag.getInt("posX"), tag.getInt("posY"), tag.getInt("posZ")),
			new Identifier(tag.getString("blockMod"), tag.getString("blockName")),
			tag.getInt("metadata"),
			tag.getInt("flag"),
			tag.getBoolean("hasTE") ? tag.getCompound("tileEntity") : null);
	}

	@Nullable
	private static CompoundTag getTileNBT(@Nullable BlockEntity te) {
		if (te == null) {
			return null;
		}

		CompoundTag nbt = new CompoundTag();
		te.toTag(nbt);

		return nbt;
	}

	public BlockState getCurrentBlock() {
		return getWorld().getBlockState(getPos());
	}

	public IWorld getWorld() {
		IWorld world = this.world != null ? this.world.get() : null;

		if (world == null) {
			world = ServerLifecycleHooks.getCurrentServer().getWorld(DimensionType.byRawId(getDimId()));
			this.world = new WeakReference<>(world);
		}

		return world;
	}

	public void setWorld(IWorld world) {
		this.world = new WeakReference<>(world);
	}

	public BlockState getReplacedBlock() {
		if (this.replacedBlock == null) {
			// Patchwork: simplify to a static reference
			this.replacedBlock = Block.getStateFromRawId(getMeta());
		}

		return this.replacedBlock;
	}

	public void setReplacedBlock(BlockState replacedBlock) {
		this.replacedBlock = replacedBlock;
	}

	@Nullable
	public BlockEntity getTileEntity() {
		return getNbt() != null ? BlockEntity.createFromTag(getNbt()) : null;
	}

	public boolean restore() {
		return restore(false);
	}

	public boolean restore(boolean force) {
		return restore(force, true);
	}

	public boolean restore(boolean force, boolean notifyNeighbors) {
		return restoreToLocation(getWorld(), getPos(), force, notifyNeighbors);
	}

	public boolean restoreToLocation(IWorld world, BlockPos pos, boolean force, boolean notifyNeighbors) {
		BlockState current = getCurrentBlock();
		BlockState replaced = getReplacedBlock();
		int flags = notifyNeighbors ? Constants.BlockFlags.DEFAULT : Constants.BlockFlags.BLOCK_UPDATE;

		if (current != replaced) {
			if (force) {
				world.setBlockState(pos, replaced, flags);
			} else {
				return false;
			}
		}

		world.setBlockState(pos, replaced, flags);

		if (world instanceof World) {
			((World) world).updateListeners(pos, current, replaced, flags);
		}

		BlockEntity te = null;

		if (getNbt() != null) {
			te = world.getBlockEntity(pos);

			if (te != null) {
				te.fromTag(getNbt());
				te.markDirty();
			}
		}

		if (DEBUG) {
			// TODO: Registry delegates
			// System.out.printf("Restored BlockSnapshot with data [World: %s ][Location: %d,%d,%d ][State: %s ][Block: %s ][TileEntity: %s ][force: %s ][notifyNeighbors: %s]", world.getLevelProperties().getLevelName(), pos.getX(), pos.getY(), pos.getZ(), replaced, replaced.getBlock().delegate.name(), te, force, notifyNeighbors);
		}

		return true;
	}

	public void writeToNBT(CompoundTag compound) {
		compound.putString("blockMod", getRegistryName().getNamespace());
		compound.putString("blockName", getRegistryName().getPath());
		compound.putInt("posX", getPos().getX());
		compound.putInt("posY", getPos().getY());
		compound.putInt("posZ", getPos().getZ());
		compound.putInt("flag", getFlag());
		compound.putInt("dimension", getDimId());
		compound.putInt("metadata", getMeta());

		compound.putBoolean("hasTE", getNbt() != null);

		if (getNbt() != null) {
			compound.put("tileEntity", getNbt());
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		final BlockSnapshot other = (BlockSnapshot) obj;

		return this.getMeta() == other.getMeta()
			&& this.getDimId() == other.getDimId()
			&& this.getPos().equals(other.getPos())
			&& this.getRegistryName().equals(other.getRegistryName())
			&& Objects.equals(this.getNbt(), other.getNbt());
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 73 * hash + this.getMeta();
		hash = 73 * hash + this.getDimId();
		hash = 73 * hash + this.getPos().hashCode();
		hash = 73 * hash + this.getRegistryName().hashCode();
		hash = 73 * hash + Objects.hashCode(this.getNbt());

		return hash;
	}

	public BlockPos getPos() {
		return pos;
	}

	public int getDimId() {
		return dimId;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	@Nullable
	public CompoundTag getNbt() {
		return nbt;
	}

	public Identifier getRegistryName() {
		return registryName;
	}

	public int getMeta() {
		return meta;
	}
}
