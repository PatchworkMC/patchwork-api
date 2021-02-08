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

import net.minecraftforge.common.capabilities.CapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;

import net.patchworkmc.impl.capability.BaseCapabilityProvider;
import net.patchworkmc.impl.capability.CapabilityProviderHolder;
import net.patchworkmc.impl.capability.IForgeTileEntityDuck;

@Mixin(BlockEntity.class)
public class MixinBlockEntity implements CapabilityProviderHolder, IForgeTileEntityDuck {
	@Unique
	private final CapabilityProvider<?> internalProvider = new BaseCapabilityProvider<>(BlockEntity.class, this);

	@NotNull
	@Override
	public CapabilityProvider<?> patchwork$getCapabilityProvider() {
		return internalProvider;
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void patchwork$callGatherCaps(BlockEntityType<?> type, CallbackInfo ci) {
		this.gatherCapabilities();
	}

	@Inject(method = "writeIdentifyingData", at = @At("TAIL"))
	private void patchwork$callSerializeCaps(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
		if (getCapabilities() != null) {
			tag.put("ForgeCaps", serializeCaps());
		}
	}

	@Inject(method = "fromTag", at = @At("TAIL"))
	public void patchwork$callDeserializeCaps(BlockState state, CompoundTag tag, CallbackInfo ci) {
		if (getCapabilities() != null && tag.contains("ForgeCaps")) {
			deserializeCaps(tag.getCompound("ForgeCaps"));
		}
	}

	@Inject(method = "markRemoved", at = @At("TAIL"))
	private void patchwork$invalidateCapsOnRemoval(CallbackInfo ci) {
		this.invalidateCaps();
	}

	@Override
	public void onChunkUnloaded() {
		this.invalidateCaps();
	}
}
