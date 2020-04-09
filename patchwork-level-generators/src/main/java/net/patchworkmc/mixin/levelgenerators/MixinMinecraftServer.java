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

package net.patchworkmc.mixin.levelgenerators;

import java.util.concurrent.Executor;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;

import net.patchworkmc.impl.levelgenerators.ChunkManagerValues;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
	@Shadow
	@Final
	private Executor workerExecutor;

	@Inject(at = @At("HEAD"), method = "createWorlds")
	private void createWorlds(WorldSaveHandler worldSaveHandler, LevelProperties properties, LevelInfo levelInfo, WorldGenerationProgressListener progressListener, CallbackInfo info) {
		ChunkManagerValues.server = (MinecraftServer) (Object) this;
		ChunkManagerValues.file = worldSaveHandler.getWorldDir();
		ChunkManagerValues.dataFixer = worldSaveHandler.getDataFixer();
		ChunkManagerValues.structureManager = worldSaveHandler.getStructureManager();
		ChunkManagerValues.workerExecutor = this.workerExecutor;
		ChunkManagerValues.viewDistance = ChunkManagerValues.server.getPlayerManager().getViewDistance();
		ChunkManagerValues.progressListener = progressListener;
	}
}
