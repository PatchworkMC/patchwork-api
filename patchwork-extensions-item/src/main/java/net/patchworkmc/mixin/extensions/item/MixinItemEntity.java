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

package net.patchworkmc.mixin.extensions.item;

import net.minecraftforge.common.extensions.IForgeItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;

import net.patchworkmc.api.extensions.item.ItemEntityLifespanAccess;

@SuppressWarnings("ConstantConditions")
@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity implements ItemEntityLifespanAccess {
	public int lifespan; // NOTE: use of lifespan in tick() and onPlayerCollision() is handled by events-entity

	protected MixinItemEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	@Shadow
	public abstract ItemStack getStack();

	@ModifyConstant(method = "setDespawnImmediately", constant = @Constant(intValue = 5999))
	private int patchwork$useCustomLifespan(int in) {
		return ((IForgeItemStack) (Object) getStack()).getEntityLifespan(world) - 1;
	}

	@Inject(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V", at = @At("TAIL"))
	private void patchwork$setInitialLifespan(World world, double x, double y, double z, ItemStack stack, CallbackInfo ci) {
		this.lifespan = (stack.getItem() == null ? 6000 : ((IForgeItemStack) (Object) getStack()).getEntityLifespan(world));
	}

	@Override
	public int patchwork$getLifespan() {
		return lifespan;
	}

	@Override
	public void patchwork$setLifespan(int lifespan) {
		this.lifespan = lifespan;
	}

	@Inject(method = "writeCustomDataToTag", at = @At("HEAD"))
	public void patchwork$storeLifespan(CompoundTag tag, CallbackInfo ci) {
		tag.putInt("Lifespan", lifespan);
	}

	@Inject(method = "readCustomDataFromTag", at = @At("HEAD"))
	public void patchwork$loadLifespan(CompoundTag tag, CallbackInfo ci) {
		if (tag.contains("Lifespan")) {
			lifespan = tag.getInt("Lifespan");
		}
	}
}
