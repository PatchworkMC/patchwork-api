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

package net.patchworkmc.mixin.event.entity.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import net.patchworkmc.impl.event.entity.EntityEvents;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {
	@Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;",
			at = @At(value = "RETURN", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void onDropItem(ItemStack itemStack, boolean bl, boolean bl2, CallbackInfoReturnable<ItemEntity> ci, double y, ItemEntity itemEntity) {
		// Note: This is implemented slightly differently from forge, since forge only calls this event on
		// dropSelectedItem(boolean) and dropItem(ItemStack, boolean), but this way makes it much nicer to implement
		// and should produce the same behavior for modders

		if (EntityEvents.onPlayerTossEvent((PlayerEntity) (Object) this, itemEntity)) {
			ci.setReturnValue(null);
		}
	}
}
