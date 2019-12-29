/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2019, 2019
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

package com.patchworkmc.mixin.capability;

import javax.annotation.Nonnull;

import net.minecraftforge.common.capabilities.CapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import com.patchworkmc.impl.capability.BaseCapabilityProvider;
import com.patchworkmc.impl.capability.CapabilityProviderHolder;

@Mixin(ItemStack.class)
public class ItemStackMixin implements CapabilityProviderHolder {

	private final CapabilityProvider<ItemStack> provider = new BaseCapabilityProvider<>(ItemStack.class, (ItemStack) (Object) this);

	@Nonnull
	@Override
	public CapabilityProvider<ItemStack> getCapabilityProvider() {
		return provider;
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void initializeCapabilities(CallbackInfo callbackInfo) {
		// TODO: Parent
		provider.gatherCapabilities();
	}

	@Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"))
	private void deserializeCapabilities(CompoundTag tag, CallbackInfo callbackInfo) {
		if (tag.containsKey("ForgeCaps")) {
			provider.deserializeCaps(tag.getCompound("ForgeCaps"));
		}
	}

	@Inject(method = "toTag", at = @At(value = "RETURN"))
	private void serializeCapabilities(CompoundTag tag, CallbackInfoReturnable<CompoundTag> callbackInfoReturnable) {
		CompoundTag compoundTag = provider.serializeCaps();

		if (compoundTag != null && !compoundTag.isEmpty()) {
			tag.put("ForgeCaps", compoundTag);
		}
	}
}
