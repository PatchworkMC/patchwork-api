package net.patchworkmc.mixin.event.entity.entity;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(WorldChunk.class)
public class MixinWorldChunk {
	@Shadow
	@Final
	private ChunkPos pos;

	@Inject(method = "addEntity", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/entity/Entity;updateNeeded:Z",
			ordinal = 0))
	private void patchwork$fireEntityEnteringChunk(Entity entity, CallbackInfo ci) {
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityEvent.EnteringChunk(entity, this.pos.x, this.pos.z, entity.chunkX, entity.chunkZ));
	}
}
