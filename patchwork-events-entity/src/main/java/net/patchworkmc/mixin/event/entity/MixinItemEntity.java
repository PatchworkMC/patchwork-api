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

package net.patchworkmc.mixin.event.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import net.patchworkmc.impl.event.entity.EntityEvents;

@Mixin(ItemEntity.class)
public class MixinItemEntity {
	@Unique
	private final String ON_PLAYER_COLLISION = "onPlayerCollision";

	@Unique
	private final ThreadLocal<ItemStack> copy = new ThreadLocal<>();

	@Unique
	private final ThreadLocal<Integer> result = new ThreadLocal<>();

	@Shadow
	private int pickupDelay;

	@Inject(method = ON_PLAYER_COLLISION, at = @At(value = "INVOKE", target = "net/minecraft/entity/ItemEntity.getStack()Lnet/minecraft/item/ItemStack;"), cancellable = true)
	public void hookOnPlayerCollide(PlayerEntity player, CallbackInfo ci) {
		if (pickupDelay > 0) {
			ci.cancel();
		}
	}

	@Inject(method = ON_PLAYER_COLLISION, at = @At(value = "INVOKE_ASSIGN", target = "net/minecraft/item/ItemStack.getCount()I"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void hookOnItemPickup(PlayerEntity player, CallbackInfo ci, ItemStack stack) {
		int hook = EntityEvents.onItemPickup((ItemEntity) (Object) this, player);

		if (hook < 0) {
			ci.cancel();
		}

		result.set(hook);
		copy.set(stack.copy());
	}

	@Redirect(method = ON_PLAYER_COLLISION, at = @At(value = "INVOKE", target = "net/minecraft/entity/player/PlayerInventory.insertStack(Lnet/minecraft/item/ItemStack;)Z"))
	public boolean redirectOnInsertStack(PlayerInventory inventory, ItemStack stack) {
		return result.get() == 1 || stack.getCount() <= 0 || inventory.insertStack(stack);
	}

	@Redirect(method = ON_PLAYER_COLLISION, at = @At(value = "INVOKE", target = "net/minecraft/entity/player/PlayerEntity.sendPickup(Lnet/minecraft/entity/Entity;I)V"))
	public void redirectOnItemPickup(PlayerEntity player, Entity entity, int quantity) {
		copy.get().setCount(copy.get().getCount() - quantity);
		EntityEvents.onPlayerItemPickup(player, (ItemEntity) entity, copy.get());
	}

	@Inject(method = ON_PLAYER_COLLISION, at = @At(value = "INVOKE", target = "net/minecraft/entity/ItemEntity.remove()V"))
	public void hookAfterIsEmptyCheck(PlayerEntity player, CallbackInfo ci) {
		player.sendPickup((ItemEntity) (Object) this, ((ItemEntity) (Object) this).getStack().getCount());
	}
}
