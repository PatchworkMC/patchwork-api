package net.patchworkmc.mixin.containers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.Direction;

import net.patchworkmc.impl.capability.PatchworkGetCapability;

@Mixin(AbstractFurnaceBlockEntity.class)
public class MixinAbstractFurnaceBlockEntity extends BlockEntity implements PatchworkGetCapability {
	@Unique
	private LazyOptional<? extends IItemHandler>[] itemHandlers = SidedInvWrapper.create((SidedInventory) this, Direction.UP, Direction.DOWN, Direction.NORTH);

	public MixinAbstractFurnaceBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	@Override
	public <T> LazyOptional<T> patchwork$getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (!this.removed && side != null && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (side == Direction.UP) {
				return itemHandlers[0].cast();
			} else if (side == Direction.DOWN) {
				return itemHandlers[1].cast();
			} else {
				return itemHandlers[2].cast();
			}
		}
		return null;
	}

	@Override
	public void markRemoved() {
		super.markRemoved();
		for (int i = 0; i < itemHandlers.length; i++) {
			itemHandlers[i].invalidate();
		}
	}
}
