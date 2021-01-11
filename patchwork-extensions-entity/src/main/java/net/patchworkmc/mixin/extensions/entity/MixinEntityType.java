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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntityType;

import net.patchworkmc.impl.extensions.entity.PatchworkEntityTypeExtensions;

@Mixin(EntityType.class)
public class MixinEntityType implements PatchworkEntityTypeExtensions {
	@Unique
	private Integer updateInterval = null;
	@Unique
	private Integer trackingRange = null;
	@Unique
	private Boolean shouldRecieveVelocityUpdates = null;

	@Inject(method = "getMaxTrackDistance", at = @At("HEAD"), cancellable = true)
	private void hookGetMaxTrackingDistance(CallbackInfoReturnable<Integer> cir) {
		if (trackingRange != null) {
			cir.setReturnValue(trackingRange);
		}
	}

	@Inject(method = "getTrackTickInterval", at = @At("HEAD"), cancellable = true)
	private void hookGetTrackTickInterval(CallbackInfoReturnable<Integer> cir) {
		if (updateInterval != null) {
			cir.setReturnValue(updateInterval);
		}
	}

	@Inject(method = "alwaysUpdateVelocity", at = @At("HEAD"), cancellable = true)
	private void hookAlwaysUpdateVelocity(CallbackInfoReturnable<Boolean> cir) {
		if (shouldRecieveVelocityUpdates != null) {
			cir.setReturnValue(shouldRecieveVelocityUpdates);
		}
	}

	@Override
	public void patchwork$setUpdateInterval(int interval) {
		this.updateInterval = interval;
	}

	@Override
	public void patchwork$setTrackingRange(int range) {
		this.trackingRange = range;
	}

	@Override
	public void patchwork$setShouldReceiveVelocityUpdates(boolean value) {
		this.shouldRecieveVelocityUpdates = value;
	}
}
