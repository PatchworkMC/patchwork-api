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

package net.patchworkmc.mixin.registries;

import net.minecraftforge.registries.IForgeRegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

import net.patchworkmc.impl.registries.ExtendedForgeRegistryEntry;
import net.patchworkmc.impl.registries.Identifiers;

@Mixin(ChunkGeneratorType.class)
public class MixinChunkGeneratorType implements ExtendedForgeRegistryEntry<ChunkGeneratorType> {
	@Unique
	private Identifier registryName;

	@Override
	public ChunkGeneratorType setRegistryName(Identifier name) {
		this.registryName = name;

		return (ChunkGeneratorType)(Object)this;
	}

	public Identifier getRegistryName() {
		ChunkGeneratorType<?, ?> chunkGeneratorType = (ChunkGeneratorType<?, ?>) (Object) this;

		return Identifiers.getOrFallback(Registry.CHUNK_GENERATOR_TYPE, chunkGeneratorType, registryName);
	}

	public Class<ChunkGeneratorType> getRegistryType() {
		return ChunkGeneratorType.class;
	}
}
