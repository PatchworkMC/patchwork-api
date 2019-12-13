package com.patchworkmc.mixin.event.world;

import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkWatchEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThreadedAnvilChunkStorage.class)
public class MixinThreadedAnvilChunkStorage {
	@Shadow
	private ServerWorld world;

	@Inject(method = "sendWatchPackets", at = @At("HEAD"))
	private void fireWatchEvents(ServerPlayerEntity player, ChunkPos pos, Packet<?>[] packets, boolean withinMaxWatchDistance, boolean withinViewDistance, CallbackInfo callback) {
		if(withinViewDistance && !withinMaxWatchDistance) {
			ChunkWatchEvent.Watch event = new ChunkWatchEvent.Watch(player, pos, world);

			MinecraftForge.EVENT_BUS.post(event);
		}
	}
}
