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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import net.patchworkmc.impl.event.entity.EntityEventsOld;

@Mixin(Entity.class)
public abstract class MixinEntity {
	@Shadow
	private float standingEyeHeight;

	@Shadow
	private EntityDimensions dimensions;

	@Shadow
	protected abstract float getEyeHeight(EntityPose pose, EntityDimensions dimensions);

	@Inject(method = "<init>", at = @At("RETURN"))
	public void hookConstructor(EntityType<?> type, World world, CallbackInfo ci) {
		Entity entity = (Entity) (Object) this;

		this.standingEyeHeight = EntityEventsOld.getEyeHeight(entity, EntityPose.STANDING, dimensions, getEyeHeight(EntityPose.STANDING, dimensions));

		EntityEventsOld.onEntityConstruct(entity);
	}

	@Inject(method = "changeDimension",
			at = @At("HEAD"),
			cancellable = true
	)
	void patchwork_fireTravelToDimensionEventChangeDimension(DimensionType newDimension, CallbackInfoReturnable<Entity> cir) {
		if (!EntityEventsOld.onTravelToDimension((Entity) (Object) this, newDimension)) {
			cir.setReturnValue(null);
		}
	}
}
