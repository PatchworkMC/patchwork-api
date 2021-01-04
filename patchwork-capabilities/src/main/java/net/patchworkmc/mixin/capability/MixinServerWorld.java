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

package net.patchworkmc.mixin.capability;

import java.util.List;
import java.util.concurrent.Executor;

import net.minecraftforge.common.util.WorldCapabilityData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;

import net.patchworkmc.impl.capability.CapabilityProviderHolder;

@Mixin(ServerWorld.class)
public abstract class MixinServerWorld implements CapabilityProviderHolder {
	@Shadow
	public abstract PersistentStateManager getPersistentStateManager();

	// this is probably impl detail, if a mod uses this it will need a duck accessor for it
	@SuppressWarnings("FieldCanBeLocal")
	@Unique
	private WorldCapabilityData capabilityData;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void patchwork$initCapabilities(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> registryKey, DimensionType dimensionType, WorldGenerationProgressListener worldGenerationProgressListener, ChunkGenerator chunkGenerator, boolean debugWorld, long l, List<Spawner> list, boolean bl, CallbackInfo ci) {
		// technically this is a protected util method but i doubt anyone is calling this
		this.gatherCapabilities();
		capabilityData = this.getPersistentStateManager().getOrCreate(() -> new WorldCapabilityData(getCapabilities()), WorldCapabilityData.ID);
		capabilityData.setCapabilities(getCapabilities());
	}
}
