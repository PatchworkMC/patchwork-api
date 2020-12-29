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

import net.minecraftforge.registries.ForgeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.util.registry.SimpleRegistry;

import net.patchworkmc.impl.registries.VanillaRegistry;

@Mixin(SimpleRegistry.class)
public abstract class MixinSimpleRegistry implements VanillaRegistry {
	@Unique
	private ForgeRegistry forgeRegistry;

	@Override
	public boolean patchwork$setForgeRegistry(ForgeRegistry forgeRegistry) {
		if (this.forgeRegistry == null) {
			this.forgeRegistry = forgeRegistry;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public ForgeRegistry patchwork$getForgeRegistry() {
		return this.forgeRegistry;
	}
}
