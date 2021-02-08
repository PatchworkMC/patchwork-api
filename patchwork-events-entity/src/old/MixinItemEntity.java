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

package net.patchworkmc.mixin.event.entity.old;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

import net.patchworkmc.impl.event.entity.EntityEventsOld;
import net.patchworkmc.impl.event.entity.PlayerEvents;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity {
	@Shadow
	public abstract ItemStack getStack();

	@Shadow
	private int pickupDelay;

	// TODO: Mods can change lifespan value via ItemStack#getEntityLifespan, and this must be set in
	// TODO: "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V"
	// TODO: See: https://github.com/PatchworkMC/YarnForge/blob/af120a35c3e6951b7ef4318801b6b8dce6fc5420/patches/minecraft/net/minecraft/entity/ItemEntity.java.patch#L18

	@Unique
	public int lifespan = 6000;

	@Unique
	private int itemPickupEventResult;

	@Unique
	private int preItemPickupEventStackCount;

	@Unique
	private ItemStack copy;

	// Forge just returns early from the item pickup event if the item has pickup delay, presumably
	// to keep it's events from being called. To maintain compatibility with potential
	// Fabric mods that might want to still have this method called from items that can't
	// be picked up yet, we'll just skip calling the events.
	@Unique
	private boolean hasPickupDelay;

	@Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
	private void patchwork_checkForPickupDelay(PlayerEntity player, CallbackInfo ci) {
		hasPickupDelay = this.pickupDelay > 0;
	}

	@Inject(method = "onPlayerCollision",
					at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I"),
					cancellable = true
	)
	private void patchwork_fireItemPickupEvent(PlayerEntity player, CallbackInfo ci) {
		if (!hasPickupDelay) {
			preItemPickupEventStackCount = getStack().getCount();
			itemPickupEventResult = PlayerEvents.onItemPickup(player, (ItemEntity) (Object) this);

			if (itemPickupEventResult < 0) {
				ci.cancel();
			}

			copy = getStack().copy();
		}
	}

	@Redirect(method = "onPlayerCollision",
					at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;insertStack(Lnet/minecraft/item/ItemStack;)Z")
	)
	private boolean patchwork_giveItemConditions(PlayerInventory playerInventory, ItemStack stack) {
		if (hasPickupDelay) {
			return playerInventory.insertStack(stack); // Haven't processed the event because Forge wouldn't.
		} else {
			return itemPickupEventResult == 1 || preItemPickupEventStackCount <= 0 || playerInventory.insertStack(stack);
		}
	}

	@Inject(method = "onPlayerCollision",
					at = @At(
									value = "INVOKE",
									shift = Shift.BEFORE,
									ordinal = 0,
									target = "net/minecraft/item/ItemStack.isEmpty()Z"
					)
	)
	private void patchwork_firePlayerItemPickupEvent(PlayerEntity player, CallbackInfo ci) {
		if (!hasPickupDelay) {
			copy.setCount(copy.getCount() - getStack().getCount());
			PlayerEvents.firePlayerItemPickupEvent(player, (ItemEntity) (Object) this, copy);
		}
	}

	@ModifyConstant(method = "tick", constant = @Constant(intValue = 6000))
	private int patchwork_dynamicLifespan(int originalValue) {
		return this.lifespan;
	}

	@Redirect(method = "tick",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;remove()V", ordinal = 1))
	private void patchwork_fireItemExpireEvent(ItemEntity entity) {
		int additionalLifespan = EntityEventsOld.onItemExpire(entity, this.getStack());

		if (additionalLifespan < 0) {
			entity.remove();
		} else {
			this.lifespan += additionalLifespan;
		}

		// The forge implementation is able to call entity.remove() twice. Not sure if it's a bug or not.
		if (this.getStack().isEmpty()) {
			entity.remove();
		}
	}
}
