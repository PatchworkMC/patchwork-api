package com.patchworkmc.mixin.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.patchworkmc.impl.event.entity.EntityEvents;

@Mixin(ServerWorld.class)
public class MixinServerWorld {
	@Inject(method = "addPlayer", at = @At("HEAD"), cancellable = true)
	private void onAddPlayer(ServerPlayerEntity player, CallbackInfo callback) {
		World world = (World)(Object)this;

		if (EntityEvents.onEntityJoinWorld(player, world)) {
			callback.cancel();
		}
	}

	@Inject(method = "addEntity", at = @At(value = "INVOKE", target = "net/minecraft/server/world/ServerWorld.getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;"), cancellable = true)
	private void onAddEntity(Entity entity, CallbackInfoReturnable<Boolean> callback) {
		World world = (World)(Object)this;

		if (EntityEvents.onEntityJoinWorld(entity, world)) {
			callback.setReturnValue(false);
		}
	}

	@Inject(method = "loadEntity", at = @At(value = "INVOKE", target = "net/minecraft/server/world/ServerWorld.loadEntityUnchecked(Lnet/minecraft/entity/Entity;)V"), cancellable = true)
	private void onLoadEntity(Entity entity, CallbackInfoReturnable<Boolean> callback) {
		World world = (World)(Object)this;

		if (EntityEvents.onEntityJoinWorld(entity, world)) {
			callback.setReturnValue(false);
		}
	}
}
