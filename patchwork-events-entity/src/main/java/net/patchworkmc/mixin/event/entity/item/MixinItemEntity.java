package net.patchworkmc.mixin.event.entity.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.patchworkmc.api.extensions.item.ItemEntityLifespanAccess;
import net.patchworkmc.impl.event.entity.EntityEvents;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity implements ItemEntityLifespanAccess {
	@Shadow
	public abstract ItemStack getStack();

	public MixinItemEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	@ModifyConstant(method = "tick()V", constant = @Constant(intValue = 6000))
	private int patchwork$useLifespan(int in) {
		return patchwork$getLifespan();
	}

	@Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;remove()V", ordinal = 1))
	private void patchwork$onItemExpire(ItemEntity itemEntity) {
		int hook = EntityEvents.onItemExpire((ItemEntity) (Object) this, this.getStack());

		if (hook < 0) {
			this.remove();
		} else {
			this.patchwork$setLifespan(this.patchwork$getLifespan() + hook);
		}
	}
}
