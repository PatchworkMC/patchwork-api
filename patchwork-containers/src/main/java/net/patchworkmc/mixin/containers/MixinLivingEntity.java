package net.patchworkmc.mixin.containers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.EntityEquipmentInvWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.patchworkmc.impl.capability.PatchworkGetCapability;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity implements PatchworkGetCapability {
	@Unique
	private final LazyOptional<?>[] handlers = EntityEquipmentInvWrapper.create((LivingEntity) (Object) this);

	public MixinLivingEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	@Shadow
	public abstract boolean isAlive();

	@Override
	public <T> LazyOptional<T> patchwork$getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (isAlive() && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (side == null) {
				return handlers[2].cast();
			} else if (side.getAxis().isVertical()) {
				return handlers[0].cast();
			} else {
				return handlers[1].cast();
			}
		}
		return null;
	}

	@Override
	public void remove() {
		super.remove();
		for (LazyOptional<?> handler : handlers) {
			handler.invalidate();
		}
	}
}
