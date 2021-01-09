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

import net.minecraftforge.common.extensions.IForgeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

@Mixin(ClientWorld.class)
public class MixinClientWorld {
	/**
	 * Before running a tick as a non-passenger, check if this entity allows updating via
	 * {@link IForgeEntity#canUpdate()}. If it does not, cancel the call to {@link Entity#tick()}.
	 */
	@Redirect(method = "tickEntity(Lnet/minecraft/entity/Entity;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V", ordinal = 0))
	private void onNonPassengerTick(Entity entity) {
		if (((IForgeEntity) entity).canUpdate()) {
			entity.tick();
		}
	}

	/**
	 * Hook the end of client-side entity adding, notifying any forge mods that have implemented
	 * {@link IForgeEntity#onAddedToWorld()}.
	 */
	@Inject(method = "addEntityPrivate", at = @At("TAIL"))
	private void onAddEntity(int entityId, Entity entity, CallbackInfo ci) {
		((IForgeEntity) entity).onAddedToWorld();
	}

	/**
	 * Hook the end of client-side entity removing, notifying any forge mods that have implemented
	 * {@link IForgeEntity#onRemovedFromWorld()}.
	 */
	@Inject(method = "finishRemovingEntity", at = @At("TAIL"))
	private void onRemoveEntity(Entity entity, CallbackInfo ci) {
		((IForgeEntity) entity).onRemovedFromWorld();
	}
}
