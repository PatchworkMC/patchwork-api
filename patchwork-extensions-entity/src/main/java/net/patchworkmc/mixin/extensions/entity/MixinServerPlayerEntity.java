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
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity {
	private static final String PERSISTED_NBT_TAG = "PlayerPersisted";

	/**
	 * If drops are being captured by the {@link IForgeEntity}, do not spawn the entities in the world. Instead, store
	 * them to the current {@link IForgeEntity#captureDrops()}.
	 */
	@Redirect(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z", ordinal = 0))
	private boolean hookDropItemForCapture(World world, Entity entity) {
		ItemEntity itemEntity = (ItemEntity) entity;
		IForgeEntity forgeEntity = (IForgeEntity) this;

		if (forgeEntity.captureDrops() != null) {
			forgeEntity.captureDrops().add(itemEntity);
			return true;
		} else {
			return world.spawnEntity(itemEntity);
		}
	}

	/**
	 * Copy {@link IForgeEntity}'s persistent data to the new player entity on respawn.
	 */
	@Inject(method = "copyFrom(Lnet/minecraft/server/network/ServerPlayerEntity;Z)V", at = @At("TAIL"))
	private void onPlayerCopy(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
		CompoundTag oldPersistentData = ((IForgeEntity) oldPlayer).getPersistentData();

		if (oldPersistentData.contains(PERSISTED_NBT_TAG)) {
			((IForgeEntity) this).getPersistentData().put(PERSISTED_NBT_TAG, oldPersistentData.get(PERSISTED_NBT_TAG));
		}
	}

	/**
	 * Replaces the constant {@code false} in {@code this.removed = false;} with the current value of {@code this.removed}.
	 * This nullifies the action of this line, and allows us to control the revival instead with an inject.
	 */
	@ModifyConstant(method = {"moveToWorld(Lnet/minecraft/server/world/ServerWorld;)Lnet/minecraft/entity/Entity;", "teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V"},
			slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendCommandTree(Lnet/minecraft/server/network/ServerPlayerEntity;)V", ordinal = 0),
					to = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setWorld(Lnet/minecraft/world/World;)V", ordinal = 0)),
			constant = @Constant(intValue = 0))
	private int nullifyRemovedAssignment(int constant) {
		return ((Entity) (Object) this).removed ? 1 : 0;
	}

	/**
	 * Handles reviving the entity, as an alternative to the nullified {@code this.removed = false;}. By default, this
	 * will just run {@code this.removed = false;} anyways, but forge-added entities can provide an alternative implementation.
	 */
	@Inject(method = "moveToWorld",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getTeleportTarget(Lnet/minecraft/server/world/ServerWorld;)Lnet/minecraft/world/TeleportTarget;", ordinal = 0))
	private void onMoveReviveEntity(CallbackInfoReturnable<Entity> ci) {
		((IForgeEntity) this).revive();
	}

	/**
	 * Handles reviving the entity, as an alternative to the nullified {@code this.removed = false;}. By default, this
	 * will just run {@code this.removed = false;} anyways, but forge-added entities can provide an alternative implementation.
	 */
	@Inject(method = "teleport",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;refreshPositionAndAngles(DDDFF)V", ordinal = 0))
	private void onTeleportReviveEntity(CallbackInfo ci) {
		((IForgeEntity) this).revive();
	}
}
