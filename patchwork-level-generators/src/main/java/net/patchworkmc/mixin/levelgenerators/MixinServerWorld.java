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

import net.minecraftforge.common.extensions.IForgeWorldType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelGeneratorType;

import net.patchworkmc.api.levelgenerators.PatchworkLevelGeneratorType;

@Mixin(ServerWorld.class)
public abstract class MixinServerWorld {
	@Redirect(method = "method_14168", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/dimension/Dimension;createChunkGenerator()Lnet/minecraft/world/gen/chunk/ChunkGenerator;"))
	private static ChunkGenerator<?> createChunkGenerator(Dimension dimension, WorldSaveHandler saveHandler, Executor executor, MinecraftServer minecraftServer, WorldGenerationProgressListener progressListener, World world, Dimension dimensionArg) {
		LevelGeneratorType generatorType = world.getLevelProperties().getGeneratorType();

		if (generatorType instanceof PatchworkLevelGeneratorType) {
			return ((IForgeWorldType) generatorType).createChunkGenerator(world);
		} else {
			return dimension.createChunkGenerator();
		}
	}
}
