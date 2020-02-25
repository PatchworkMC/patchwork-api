package com.patchworkmc.mixin.event.entity;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import com.patchworkmc.impl.event.entity.EntityEvents;

@Mixin(WorldChunk.class)
public class MixinWorldChunk {
	@Shadow
	@Final
	private ChunkPos pos;

	@Inject(method = "addEntity", at = @At(target = "Lnet/minecraft/util/math/MathHelper;floor(D)I", value = "INVOKE", ordinal = 2))
	public void hookAddEntity(Entity entity, CallbackInfo ci) {
		EntityEvents.onEnteringChunk(entity, this.pos.x, this.pos.z, entity.chunkX, entity.chunkZ);
	}
}
