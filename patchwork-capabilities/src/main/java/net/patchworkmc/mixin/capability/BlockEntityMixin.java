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

import javax.annotation.Nonnull;

import net.minecraftforge.common.capabilities.CapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;

import net.patchworkmc.impl.capability.BaseCapabilityProvider;
import net.patchworkmc.impl.capability.CapabilityProviderHolder;

@Mixin(BlockEntity.class)
public class BlockEntityMixin implements CapabilityProviderHolder {
	private final CapabilityProvider<BlockEntity> provider = new BaseCapabilityProvider<>(BlockEntity.class, (BlockEntity) (Object) this);

	@Nonnull
	@Override
	public CapabilityProvider<BlockEntity> getCapabilityProvider() {
		return provider;
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void initializeCapabilities(CallbackInfo callbackInfo) {
		gatherCapabilities();
	}

	@Inject(method = "writeIdentifyingData", at = @At("RETURN"))
	private void serializeCapabilities(CompoundTag compoundTag, CallbackInfoReturnable<CompoundTag> callbackInfoReturnable) {
		if (getCapabilities() != null) {
			compoundTag.put("ForgeCaps", serializeCaps());
		}
	}

	@Inject(method = "fromTag", at = @At("RETURN"))
	private void deserializeCapabilities(CompoundTag compoundTag, CallbackInfo callbackInfo) {
		if (getCapabilities() != null && compoundTag.contains("ForgeCaps")) {
			deserializeCaps(compoundTag.getCompound("ForgeCaps"));
		}
	}

	@Inject(method = "markRemoved", at = @At("RETURN"))
	private void invalidate(CallbackInfo callbackInfo) {
		invalidateCaps();
	}
}
