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

package com.patchworkmc.mixin.registries;

import net.minecraftforge.registries.IForgeRegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

import com.patchworkmc.impl.registries.ExtendedForgeRegistryEntry;
import com.patchworkmc.impl.registries.Identifiers;

@Mixin(SurfaceBuilder.class)
public class MixinSurfaceBuilder implements ExtendedForgeRegistryEntry<SurfaceBuilder> {
	@Unique
	private Identifier registryName;

	@Override
	public IForgeRegistryEntry<SurfaceBuilder> setRegistryName(Identifier name) {
		this.registryName = name;

		return this;
	}

	public Identifier getRegistryName() {
		SurfaceBuilder<?> surfaceBuilder = (SurfaceBuilder<?>) (Object) this;

		return Identifiers.getOrFallback(Registry.SURFACE_BUILDER, surfaceBuilder, registryName);
	}

	public Class<SurfaceBuilder> getRegistryType() {
		return SurfaceBuilder.class;
	}
}
