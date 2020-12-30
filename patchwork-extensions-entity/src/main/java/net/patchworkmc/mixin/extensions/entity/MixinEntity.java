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

package net.patchworkmc.mixin.extensions.entity;

import java.util.Collection;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;

import net.minecraftforge.common.extensions.IForgeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity implements IForgeEntity {
	@Shadow
	public abstract boolean hasVehicle();

	@Shadow
	public abstract Entity getVehicle();

	@Shadow
	public boolean removed;

	@Unique
	private Collection<ItemEntity> captureDrops = null;

	@Unique
	private boolean canUpdate;

	@Unique
	private boolean addedToWorld;

	@Redirect(method = "dropStack(Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/ItemEntity;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z", ordinal = 0))
	private boolean hookDropStackForCapture(World world, Entity entity) {
		ItemEntity itemEntity = (ItemEntity) entity;

		if (captureDrops() != null) {
			captureDrops().add(itemEntity);
			return true;
		} else {
			return world.spawnEntity(itemEntity);
		}
	}

	@Inject(method = "toTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;writeCustomDataToTag(Lnet/minecraft/nbt/CompoundTag;)V", ordinal = 0))
	private void serializeUpdate(CompoundTag tag, CallbackInfoReturnable<CompoundTag> callbackInfoReturnable) {
		tag.putBoolean("CanUpdate", canUpdate);
	}

	@Inject(method = "fromTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setGlowing(Z)V", ordinal = 0))
	private void deserializeUpdate(CompoundTag tag, CallbackInfo callbackInfo) {
		if (tag.contains("CanUpdate", 99)) {
			this.canUpdate(tag.getBoolean("CanUpdate"));
		}
	}

	@Inject(method = "tickRiding", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V", ordinal = 0), cancellable = true)
	private void onTickAttempt(CallbackInfo ci) {
		if (!canUpdate()) {
			// Replicate vanilla behavior for the rest of tickRiding, since we only want to cancel the tick() call.
			if (this.hasVehicle()) {
				this.getVehicle().updatePassengerPosition((Entity) (Object) this);
			}

			ci.cancel();
		}
	}

	@Override
	public boolean canUpdate() {
		return this.canUpdate;
	}

	@Override
	public void canUpdate(boolean value) {
		this.canUpdate = value;
	}

	@Nullable
	@Override
	public Collection<ItemEntity> captureDrops() {
		return captureDrops;
	}

	@Override
	public Collection<ItemEntity> captureDrops(@Nullable Collection<ItemEntity> value) {
		Collection<ItemEntity> ret = captureDrops;
		this.captureDrops = value;
		return ret;
	}

	@Override
	public boolean isAddedToWorld() {
		return this.addedToWorld;
	}

	@Override
	public void onAddedToWorld() {
		this.addedToWorld = true;
	}

	@Override
	public void onRemovedFromWorld() {
		this.addedToWorld = false;
	}

	@Override
	public void revive() {
		this.removed = false;
		// TODO: Once patchwork-capabilities is ported: ((CapabilityProviderHolder) this).reviveCaps();
	}
}
