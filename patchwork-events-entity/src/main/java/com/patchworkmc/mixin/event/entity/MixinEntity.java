package com.patchworkmc.mixin.event.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

import com.patchworkmc.impl.event.entity.EntityEvents;

@Mixin(Entity.class)
public abstract class MixinEntity {
	@Shadow
	private float standingEyeHeight;

	@Shadow
	private EntityDimensions dimensions;

	@Shadow
	protected abstract float getEyeHeight(EntityPose pose, EntityDimensions dimensions);

	@Inject(method = "<init>", at = @At("RETURN"))
	public void hookConstructor(EntityType<?> type, World world, CallbackInfo ci) {
		Entity entity = (Entity) (Object) this;

		this.standingEyeHeight = EntityEvents.getEyeHeight(entity, EntityPose.STANDING, dimensions, getEyeHeight(EntityPose.STANDING, dimensions));

		EntityEvents.onEntityConstruct(entity);
	}
}
