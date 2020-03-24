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

package net.patchworkmc.mixin.enumhacks;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.Item;

import net.patchworkmc.impl.enumhacks.PatchworkEnchantmentTarget;

@Mixin(targets = "net.minecraft.enchantment.EnchantmentTarget$1")
public class MixinEnchantmentTargetInner implements PatchworkEnchantmentTarget {
	@Unique
	private boolean isPatchwork = false;

	@Unique
	private Predicate<Item> predicate;

	@Inject(method = "isAcceptableItem(Lnet/minecraft/item/Item;)Z", at = @At("HEAD"), cancellable = true)
	private void checkItem(Item item, CallbackInfoReturnable<Boolean> callback) {
		if (isPatchwork) {
			callback.setReturnValue(predicate.test(item));
		}
	}

	@Override
	public void patchwork_setPredicate(Predicate<Item> predicate) {
		this.predicate = predicate;
		isPatchwork = true;
	}

	@Override
	public Lookup patchwork_getEnchantmentTargetPrivateLookup() {
		return MethodHandles.lookup();
	}
}
