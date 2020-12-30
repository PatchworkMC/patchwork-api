package net.patchworkmc.mixin.extensions.entity;

import net.minecraft.client.world.ClientWorld;

import net.minecraft.entity.Entity;

import net.minecraftforge.common.extensions.IForgeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class MixinClientWorld {
	@Inject(method = "addEntityPrivate", at = @At("TAIL"))
	private void onAddEntity(int entityId, Entity entity, CallbackInfo ci) {
		((IForgeEntity) entity).onAddedToWorld();
	}

	@Inject(method = "finishRemovingEntity", at = @At("TAIL"))
	private void onRemoveEntity(Entity entity, CallbackInfo ci) {
		((IForgeEntity) entity).onRemovedFromWorld();
	}
}
