package net.patchworkmc.mixin.extensions.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import net.minecraftforge.common.extensions.IForgeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {
	@Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;canBeRiddenInWater()Z", ordinal = 0))
	private boolean onUnderwaterRidingCheck(Entity vehicle) {
		return ((IForgeEntity) vehicle).canBeRiddenInWater((Entity) (Object) this);
	}
}
