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

package com.patchworkmc.mixin.extension;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntityType;

import com.patchworkmc.impl.extension.PatchworkEntityTypeBuilderExtensions;
import com.patchworkmc.impl.extension.PatchworkEntityTypeExtensions;

@Mixin(EntityType.Builder.class)
public class MixinEntityTypeBuilder implements PatchworkEntityTypeBuilderExtensions {
	@Unique
	private Integer updateInterval = null;
	@Unique
	private Integer trackingRange = null;
	@Unique
	private Boolean shouldRecieveVelocityUpdates = null;

	@Inject(method = "build", at = @At("RETURN"))
	private void onBuildReturn(String id, CallbackInfoReturnable<EntityType> cir) {
		PatchworkEntityTypeExtensions type = (PatchworkEntityTypeExtensions) cir.getReturnValue();

		if (updateInterval != null) {
			type.setUpdateInterval(updateInterval);
		}

		if (trackingRange != null) {
			type.setTrackingRange(trackingRange);
		}

		if (shouldRecieveVelocityUpdates != null) {
			type.setShouldReceiveVelocityUpdates(shouldRecieveVelocityUpdates);
		}
	}

	@Override
	public EntityType.Builder setUpdateInterval(int interval) {
		this.updateInterval = interval;
		return (EntityType.Builder) (Object) this;
	}

	@Override
	public EntityType.Builder setTrackingRange(int range) {
		this.trackingRange = range;
		return (EntityType.Builder) (Object) this;
	}

	@Override
	public EntityType.Builder setShouldReceiveVelocityUpdates(boolean value) {
		this.shouldRecieveVelocityUpdates = value;
		return (EntityType.Builder) (Object) this;
	}
}
