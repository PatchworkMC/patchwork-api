/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2020, 2019-2020
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.patchworkmc.mixin.event.world;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

import net.patchworkmc.impl.event.world.WorldEvents;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class MixinThreadedAnvilChunkStorage {
	@Final
	@Shadow
	private ServerWorld world;

	@Inject(method = "sendWatchPackets", at = @At("HEAD"))
	private void fireWatchEvents(ServerPlayerEntity player, ChunkPos pos, Packet<?>[] packets, boolean withinMaxWatchDistance, boolean withinViewDistance, CallbackInfo callback) {
		if (this.world == player.world && withinMaxWatchDistance != withinViewDistance) {
			WorldEvents.fireChunkWatch(withinViewDistance, player, pos, this.world);
		}
	}

	// Lambda in "private CompletableFuture<Either<Chunk, Unloaded>> loadChunk(ChunkPos chunkPos)"
	//   chunk.setLastSaveTime(this.world.getTime());
	// + MinecraftForge.EVENT_BUS.post(new ChunkEvent.Load(chunk));
	//   return Either.left(chunk);
	@SuppressWarnings("rawtypes")
	@Inject(method = "method_17256", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.AFTER, ordinal = 0, target =
			"net/minecraft/world/chunk/Chunk.setLastSaveTime(J)V"))
	public void onServerChunkLoad(ChunkPos chunkPos, CallbackInfoReturnable info, CompoundTag compoundTag, boolean bl, Chunk chunk) {
		// Fire ChunkEvent.Load on server side
		MinecraftForge.EVENT_BUS.post(new ChunkEvent.Load(chunk));
	}
}
