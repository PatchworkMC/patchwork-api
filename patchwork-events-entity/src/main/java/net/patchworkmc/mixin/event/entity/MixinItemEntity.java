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

import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import net.patchworkmc.impl.event.entity.EntityEvents;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity {
	@Shadow
	private int pickupDelay;

	@Shadow
	public abstract ItemStack getStack();

	@Unique
	public int lifespan = 6000;

	// TODO -> Forge has exposed 'lifespan' (hardcoded to 6000) through IForgeItemStack/IForgeItem#getEntityLifespan
	/*
	@Inject(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V", at = @At("RETURN"))
	private void modifyLifespan(World world, double x, double y, double z, ItemStack stack, CallbackInfo ci) {
		this.lifespan = stack.getItem() == null ? 6000 : ((IForgeItemStack) stack).getEntityLifespan(world);
	}
	 */

	// TODO -> Forge has a callback at IForgeItemStack/IForgeItem#onEntityItemUpdate
	/*
	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void onUpdate(CallbackInfo ci) {
		if ((IForgeItemStack) stack).onEntityItemUpdate((ItemEntity) (Object) this) {
			ci.cancel();
		}
	}
	 */

	@ModifyConstant(method = "tick", constant = @Constant(intValue = 6000))
	private int onTickCheckLifespan(int original) {
		return lifespan;
	}

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;remove()V"))
	private void fireOnItemExpire(ItemEntity item) {
		int hook = EntityEvents.onItemExpire(item, getStack());

		if (hook < 0) {
			item.remove();
		}
		else {
			this.lifespan += hook;
		}
	}

	@Inject(method = "writeCustomDataToTag", at = @At("HEAD"))
	private void addLifespanToTag(CompoundTag tag, CallbackInfo ci) {
		tag.putInt("Lifespan", lifespan);
	}

	@Inject(method = "readCustomDataFromTag", at = @At("HEAD"))
	private void readLifespanInTag(CompoundTag tag, CallbackInfo ci) {
		if (tag.contains("Lifespan")) {
			this.lifespan = tag.getInt("Lifespan");
		}
	}

	@Redirect(method = "onPlayerCollision", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/world/World;isClient:Z"))
	private boolean checkPickupDelay(World world) {
		return world.isClient && this.pickupDelay <= 0;
	}

	@Inject(method = "onPlayerCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;getStack()Lnet/minecraft/item/ItemStack;"), cancellable = true)
	private void fireEntityItemPickup(PlayerEntity player, CallbackInfo ci) {
		if (EntityEvents.onItemPickup((ItemEntity) (Object) this, player)) {
			ci.cancel();
		}
	}

	@ModifyConstant(method = "onPlayerCollision", constant = @Constant(intValue = 6000, ordinal = 0))
	private int onPlayerCollideCheckLifespan(int original) {
		return lifespan;
	}

	@Redirect(method = "onPlayerCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;sendPickup(Lnet/minecraft/entity/Entity;I)V"))
	private void onPlayerItemPickup(PlayerEntity entity, Entity item, int count) {
		EntityEvents.firePlayerItemPickupEvent(entity, (ItemEntity) item, getStack().copy());
	}

	@Inject(method = "onPlayerCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;remove()V"))
	private void sendPickup(PlayerEntity player, CallbackInfo ci) {
		player.sendPickup((ItemEntity) (Object) this, getStack().getCount());
	}

	/*
	@ModifyConstant(method = "setDespawnImmediately", constant = @Constant(intValue = 5999))
	private int setAge(World world) {
		return ((IForgeItemStack) stack).getEntityLifespan(((ItemEntity) (Object) this).world) - 1;
	}
	 */
}
