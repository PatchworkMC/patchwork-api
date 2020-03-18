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

import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.patchworkmc.impl.capability.CapabilityProviderHolder;

@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin {
	@Redirect(method = "deserialize", at = @At(value = "NEW", target = "net/minecraft/world/chunk/WorldChunk"))
	private static WorldChunk newWorldChunk(
			World newWorld, ChunkPos newChunkPos, Biome[] newBiomes, UpgradeData newUpgradeData, TickScheduler<Block> newTickScheduler, TickScheduler<Fluid> newTickScheduler2, long newL, @Nullable ChunkSection[] newChunkSections, @Nullable Consumer<WorldChunk> newConsumer,
			ServerWorld serverWorld, StructureManager structureManager, PointOfInterestStorage pointOfInterestStorage, ChunkPos chunkPos, CompoundTag compoundTag) {
		WorldChunk chunk = new WorldChunk(newWorld, newChunkPos, newBiomes, newUpgradeData, newTickScheduler, newTickScheduler2, newL, newChunkSections, newConsumer);
		CompoundTag level = compoundTag.getCompound("Level");

		if (level.contains("ForgeCaps")) {
			((CapabilityProviderHolder) chunk).deserializeCaps(level.getCompound("ForgeCaps"));
		}

		return chunk;
	}

	@Inject(method = "serialize", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;saveToTag(Lnet/minecraft/nbt/CompoundTag;)Z"), to = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/ProtoChunk;getEntities()Ljava/util/List;")), at = @At(value = "JUMP", opcode = Opcodes.GOTO, ordinal = 2), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void serializeCapabilities(ServerWorld serverWorld, Chunk chunk, CallbackInfoReturnable<CompoundTag> callbackInfoReturnable, ChunkPos chunkPos, CompoundTag compoundTag, CompoundTag level) {
		CompoundTag tag = ((CapabilityProviderHolder) chunk).serializeCaps();

		if (tag != null) {
			level.put("ForgeCaps", tag);
		}
	}
}
