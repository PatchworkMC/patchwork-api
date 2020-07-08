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

package net.patchworkmc.mixin.containers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import net.patchworkmc.impl.capability.PatchworkGetCapability;

@Mixin(ChestBlockEntity.class)
public class MixinChestBlockEntity extends BlockEntity implements PatchworkGetCapability {
	@Unique
	private LazyOptional<?> chestHandler;

	public MixinChestBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	@Override
	public void resetBlock() {
		super.resetBlock();

		if (chestHandler != null) {
			chestHandler.invalidate();
			chestHandler = null;
		}
	}

	@Override
	public <T> LazyOptional<T> patchwork$getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (!this.removed && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (chestHandler == null) {
				chestHandler = LazyOptional.of(this::createHandler);
			}

			return chestHandler.cast();
		}

		return null;
	}

	@Nonnull
	private IItemHandlerModifiable createHandler() {
		BlockState state = this.getCachedState();

		if (!(state.getBlock() instanceof ChestBlock)) {
			return new InvWrapper((Inventory) this);
		}

		ChestType type = state.get(ChestBlock.CHEST_TYPE);

		if (type != ChestType.SINGLE) {
			BlockPos opos = this.getPos().offset(ChestBlock.getFacing(state));
			BlockState ostate = this.getWorld().getBlockState(opos);

			if (state.getBlock() == ostate.getBlock()) {
				ChestType otype = ostate.get(ChestBlock.CHEST_TYPE);

				if (otype != ChestType.SINGLE && type != otype && state.get(ChestBlock.FACING) == ostate.get(ChestBlock.FACING)) {
					BlockEntity ote = this.getWorld().getBlockEntity(opos);

					if (ote instanceof ChestBlockEntity) {
						Inventory top = type == ChestType.RIGHT ? (Inventory) this : (Inventory) ote;
						Inventory bottom = type == ChestType.RIGHT ? (Inventory) ote : (Inventory) this;
						return new CombinedInvWrapper(new InvWrapper(top), new InvWrapper(bottom));
					}
				}
			}
		}

		return new InvWrapper((Inventory) this);
	}
}
