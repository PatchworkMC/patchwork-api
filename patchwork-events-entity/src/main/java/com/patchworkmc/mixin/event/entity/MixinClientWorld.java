package com.patchworkmc.mixin.event.entity;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.patchworkmc.impl.event.entity.EntityEvents;

@Mixin(ClientWorld.class)
public class MixinClientWorld {
	@Inject(method = "addEntityPrivate", at = @At("HEAD"), cancellable = true)
	private void onEntityAdded(int id, Entity entity, CallbackInfo callback) {
		World world = (World)(Object)this;

		if (EntityEvents.onEntityJoinWorld(entity, world)) {
			callback.cancel();
		}
	}
}
