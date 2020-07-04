package net.patchworkmc.mixin.containers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.util.math.Direction;

import net.patchworkmc.impl.capability.PatchworkGetCapability;

@Mixin(HorseBaseEntity.class)
public class MixinHorseBaseEntity implements PatchworkGetCapability {
	@Shadow
	protected BasicInventory items;

	@Unique
	private LazyOptional itemHandler;

	@Inject(method = "method_6721", at = @At("RETURN"))
	private void registerItemHandler(CallbackInfo ci) {
		this.itemHandler = LazyOptional.of(() -> new InvWrapper(this.items));
	}

	@Override
	public <T> LazyOptional<T> patchwork$getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (((LivingEntity) (Object) this).isAlive() && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && itemHandler != null)
			return itemHandler.cast();
		return null;
	}
}
