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

package net.patchworkmc.mixin.event.entity.old;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import net.patchworkmc.impl.event.entity.EntityEventsOld;

@Mixin(ServerWorld.class)
public class MixinServerWorld {
	@Inject(method = "addPlayer", at = @At("HEAD"), cancellable = true)
	private void onAddPlayer(ServerPlayerEntity player, CallbackInfo callback) {
		World world = (World) (Object) this;

		if (EntityEventsOld.onEntityJoinWorld(player, world)) {
			callback.cancel();
		}
	}

	@Inject(method = "addEntity", at = @At(value = "INVOKE", target = "net/minecraft/server/world/ServerWorld.getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;"), cancellable = true)
	private void onAddEntity(Entity entity, CallbackInfoReturnable<Boolean> callback) {
		World world = (World) (Object) this;

		if (EntityEventsOld.onEntityJoinWorld(entity, world)) {
			callback.setReturnValue(false);
		}
	}

	@Inject(method = "loadEntity", at = @At(value = "INVOKE", target = "net/minecraft/server/world/ServerWorld.loadEntityUnchecked(Lnet/minecraft/entity/Entity;)V"), cancellable = true)
	private void onLoadEntity(Entity entity, CallbackInfoReturnable<Boolean> callback) {
		World world = (World) (Object) this;

		if (EntityEventsOld.onEntityJoinWorld(entity, world)) {
			callback.setReturnValue(false);
		}
	}
}
