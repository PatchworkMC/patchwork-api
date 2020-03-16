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

package com.patchworkmc.mixin.capability;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;

import com.patchworkmc.impl.capability.CapabilityProviderHolder;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
	@Inject(method = "merge", at = @At("HEAD"), cancellable = true)
	private static void merge(ItemEntity targetEntity, ItemStack targetStack, ItemEntity sourceEntity, ItemStack sourceStack, CallbackInfo callbackInfo) {
		CapabilityProviderHolder source = (CapabilityProviderHolder) (Object) sourceStack;
		CapabilityProviderHolder target = (CapabilityProviderHolder) (Object) targetStack;

		if (!source.areCapsCompatible(target.getCapabilityProvider())) {
			callbackInfo.cancel();
		}
	}
}
