package net.patchworkmc.mixin.containers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.Direction;

import net.patchworkmc.impl.capability.PatchworkGetCapability;

@Mixin(value = LockableContainerBlockEntity.class) // Priority so we go after capabilities
public class MixinLockableContainerBlockEntity extends BlockEntity implements PatchworkGetCapability {
	@Unique
	private LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper((Inventory) this));

	public MixinLockableContainerBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	@Override
	public <T> LazyOptional<T> patchwork$getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (!this.removed && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return itemHandler.cast();
		}
		return null;
	}

	@Override
	public void markRemoved() {
		super.markRemoved();
		itemHandler.invalidate();
	}
}
