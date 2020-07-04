package net.patchworkmc.mixin.containers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.Direction;

import net.patchworkmc.impl.capability.PatchworkGetCapability;

@Mixin(StorageMinecartEntity.class)
public class MixinStorageMinecartEntity implements PatchworkGetCapability {
	@Unique
	private LazyOptional<?> itemHandler = LazyOptional.of(() -> new InvWrapper((Inventory) this));

	@Override
	public <T> LazyOptional<T> patchwork$getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (((Entity) (Object) this).isAlive() && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return itemHandler.cast();
		return null;
	}
}
