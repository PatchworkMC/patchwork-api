package net.patchworkmc.mixin.extensions.entity;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;

import net.minecraftforge.common.extensions.IForgeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class MixinServerWorld {
	@Redirect(method = "tickEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V", ordinal = 0))
	private void onNonPassengerTick(Entity entity) {
		if (((IForgeEntity) entity).canUpdate()) {
			entity.tick();
		}
	}

	@Inject(method = "loadEntityUnchecked", at = @At("TAIL"))
	private void onAddEntity(Entity entity, CallbackInfo ci) {
		((IForgeEntity) entity).onAddedToWorld();
	}

	@Inject(method = "unloadEntity", at = @At("TAIL"))
	private void onRemoveEntity(Entity entity, CallbackInfo ci) {
		((IForgeEntity) entity).onRemovedFromWorld();
	}
}
