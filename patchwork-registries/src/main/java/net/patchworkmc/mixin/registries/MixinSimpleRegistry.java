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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.SimpleRegistry;

import net.patchworkmc.impl.registries.VanillaRegistry;

@Mixin(SimpleRegistry.class)
public abstract class MixinSimpleRegistry<T> implements VanillaRegistry {
	private ForgeRegistry forgeRegistry;

	@Shadow
	private int nextId;
	@Shadow
	public abstract <V extends T> V set(int rawId, Identifier id, V entry);

	@Inject(at = @At("HEAD"), method = "add(Lnet/minecraft/util/Identifier;Ljava/lang/Object;)Ljava/lang/Object;")
	private void hookadd(Identifier id, Object entry, CallbackInfoReturnable<Object> info) {
		ForgeRegistry reg = getForgeRegistry();

		if (reg != null) {
			reg.onAdd(this.nextId, (IForgeRegistryEntry) entry, null);
		}
	}

	@Override
	public boolean setForgeRegistry(ForgeRegistry forgeRegistry) {
		if (this.forgeRegistry == null) {
			this.forgeRegistry = forgeRegistry;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public ForgeRegistry getForgeRegistry() {
		return this.forgeRegistry;
	}
}
