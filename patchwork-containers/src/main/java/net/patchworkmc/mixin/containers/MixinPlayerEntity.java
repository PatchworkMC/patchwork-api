package net.patchworkmc.mixin.containers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.PlayerArmorInvWrapper;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import net.minecraftforge.items.wrapper.PlayerOffhandInvWrapper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.Direction;

import net.patchworkmc.impl.capability.PatchworkGetCapability;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity implements PatchworkGetCapability {
	@Shadow
	@Final
	public PlayerInventory inventory;

	@Unique
	private LazyOptional<IItemHandler> playerMainHandler = LazyOptional.of(() -> new PlayerMainInvWrapper(inventory));

	@Unique
	private LazyOptional<IItemHandler> playerEquipmentHandler = LazyOptional.of(() ->
			new CombinedInvWrapper(new PlayerArmorInvWrapper(inventory), new PlayerOffhandInvWrapper(inventory)));

	@Unique
	private LazyOptional<IItemHandler> playerJoinedHandler = LazyOptional.of(() -> new PlayerInvWrapper(inventory));

	@Override
	public <T> LazyOptional<T> patchwork$getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (((LivingEntity) (Object) this).isAlive() && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (side == null) {
				return playerJoinedHandler.cast();
			} else if (side.getAxis().isVertical()) {
				return playerMainHandler.cast();
			} else {
				return playerEquipmentHandler.cast();
			}
		}
		return null;
	}
}
