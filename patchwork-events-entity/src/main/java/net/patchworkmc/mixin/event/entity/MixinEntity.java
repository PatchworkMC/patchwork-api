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

import java.util.Collection;

import net.minecraftforge.common.extensions.IForgeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;

import net.patchworkmc.impl.event.entity.EntityEvents;

@Mixin(Entity.class)
public abstract class MixinEntity implements IForgeEntity {
	@Shadow
	private float standingEyeHeight;

	@Shadow
	private EntityDimensions dimensions;

	@Shadow
	protected abstract float getEyeHeight(EntityPose pose, EntityDimensions dimensions);

	@Unique
	private Collection<ItemEntity> captureDrops = null;

	@Inject(method = "<init>", at = @At("RETURN"))
	public void hookConstructor(EntityType<?> type, World world, CallbackInfo ci) {
		Entity entity = (Entity) (Object) this;

		this.standingEyeHeight = EntityEvents.getEyeHeight(entity, EntityPose.STANDING, dimensions, getEyeHeight(EntityPose.STANDING, dimensions));

		EntityEvents.onEntityConstruct(entity);
	}

	@Redirect(method = "dropStack(Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;setToDefaultPickupDelay()V"))
	private void addToCaptureDrops(ItemEntity entity) {
		if (this.captureDrops != null) {
			this.captureDrops.add(entity);
		}
	}

	@Override
	public Collection<ItemEntity> captureDrops() {
		return captureDrops;
	}

	@Override
	public Collection<ItemEntity> captureDrops(Collection<ItemEntity> replacement) {
		Collection<ItemEntity> cache = this.captureDrops;
		this.captureDrops = replacement;
		return cache;
	}
}
