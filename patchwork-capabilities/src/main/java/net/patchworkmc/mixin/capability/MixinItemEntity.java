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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;

import net.patchworkmc.api.capability.CapabilityProviderConvertible;
import net.patchworkmc.impl.capability.CapabilityProviderHolder;

@Mixin(ItemEntity.class)
public class MixinItemEntity {
	@SuppressWarnings("ConstantConditions")
	@Inject(method = "canMerge(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z",
			at = @At("TAIL"), cancellable = true)
	private static void patchwork$checkAreCapsCompatible(ItemStack stack1, ItemStack stack2, CallbackInfoReturnable<Boolean> cir) {
		if (!((CapabilityProviderHolder) (Object) stack1)
				.areCapsCompatible(((CapabilityProviderConvertible) (Object) stack2).patchwork$getCapabilityProvider())) {
			cir.setReturnValue(false);
		}
	}
}
