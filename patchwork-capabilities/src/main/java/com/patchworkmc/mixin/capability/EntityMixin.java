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

import javax.annotation.Nonnull;

import net.minecraftforge.common.capabilities.CapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;

import com.patchworkmc.impl.capability.BaseCapabilityProvider;
import com.patchworkmc.impl.capability.CapabilityProviderHolder;

@Mixin(Entity.class)
public class EntityMixin implements CapabilityProviderHolder {
	private final CapabilityProvider<Entity> provider = new BaseCapabilityProvider<>(Entity.class, (Entity) (Object) this);

	@Shadow
	public boolean removed;

	@Nonnull
	@Override
	public CapabilityProvider<Entity> getCapabilityProvider() {
		return provider;
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void initializeCapabilities(CallbackInfo callbackInfo) {
		gatherCapabilities();
	}

	@Inject(method = "toTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;writeCustomDataToTag(Lnet/minecraft/nbt/CompoundTag;)V"))
	private void serializeCapabilities(CompoundTag tag, CallbackInfoReturnable<CompoundTag> callbackInfoReturnable) {
		CompoundTag capabilities = serializeCaps();

		if (capabilities != null) {
			tag.put("ForgeCaps", capabilities);
		}
	}

	@Inject(method = "fromTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;readCustomDataFromTag(Lnet/minecraft/nbt/CompoundTag;)V"))
	private void deserializeCapabilities(CompoundTag tag, CallbackInfo callbackInfo) {
		if (tag.containsKey("ForgeCaps")) {
			deserializeCaps(tag.getCompound("ForgeCaps"));
		}
	}

	@Inject(method = "remove", at = @At("RETURN"))
	private void onRemoved(CallbackInfo callback) {
		invalidateCaps();
	}

	// "well, my approach is that if that becomes an issue, we'll fix it"
	//  - coderbot, Patchwork Lead
	public void remove(boolean keep) {
		removed = true;

		if (!keep) {
			invalidateCaps();
		}
	}
}
