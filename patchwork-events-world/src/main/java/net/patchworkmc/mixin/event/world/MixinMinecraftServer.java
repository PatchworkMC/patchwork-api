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

import java.io.IOException;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.world.World;
import net.minecraft.world.level.ServerWorldProperties;

import net.patchworkmc.impl.event.world.WorldEvents;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer extends ReentrantThreadExecutor<ServerTask> {
	@Shadow
	@Final
	private Map<RegistryKey<World>, ServerWorld> worlds;

	public MixinMinecraftServer(String string) {
		super(string);
	}

	// Fabric usually fires the event much earlier in the method, so this is just picking a point closer to when Forge would fire it.
	@Inject(method = "createWorlds", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/SimpleRegistry;getEntries()Ljava/util/Set;"))
	private void fireLoadForOverworld(CallbackInfo info) {
		WorldEvents.onWorldLoad(worlds.get(World.OVERWORLD));
	}

	@Redirect(method = "shutdown", at = @At(value = "INVOKE", target = "net/minecraft/server/world/ServerWorld.close()V"))
	private void proxyClose(ServerWorld world) throws IOException {
		WorldEvents.onWorldUnload(world);
		world.close();
	}

	// TODO: consider adding a shift to before obtaining the ChunkManager to match forge more closely
	// I don't think it'll make much of a difference.
	@Inject(method = "setupSpawn", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/chunk/ChunkGenerator;getBiomeSource()Lnet/minecraft/world/biome/source/BiomeSource;"))
	private static void onCreateWorldSpawn(ServerWorld world, ServerWorldProperties serverWorldProperties, boolean bonusChest, boolean debugWorld, boolean bl, CallbackInfo info) {
		if (WorldEvents.onCreateWorldSpawn(world, serverWorldProperties)) {
			info.cancel();
		}
	}

	// TODO: DimensionManager, and move this into a separate module/*
	/*@Inject(method = "createWorlds", at = @At(value = "HEAD"))
	private void hookCreateWorldsForDimensionRegistration(CallbackInfo info) {
		DimensionManager.fireRegister();
	}

	@Shadow
	private int ticks;

	@Redirect(method = "tickWorlds", at = @At(value = "INVOKE_STRING", target = "net/minecraft/util/profiler/DisableableProfiler.swap (Ljava/lang/String;)V", args = { "ldc=connection" }))
	private void hookTickWorldsForDimensionUnload(DisableableProfiler profiler, String section) {
		MinecraftServer server = (MinecraftServer) (Object) this;
		profiler.swap("dim_unloading");
		DimensionManager.unloadWorlds(server, this.ticks % 200);
		profiler.swap(section);
	}

	@Redirect(method = "getWorld", at = @At(value = "INVOKE", target = "java/util/Map.get (Ljava/lang/Object;)Ljava/lang/Object;"))
	private Object hookGetWorld(Map worlds, Object type) {
		MinecraftServer server = (MinecraftServer) (Object) this;
		return DimensionManager.getWorld(server, type, true, true);
	}*/
}
