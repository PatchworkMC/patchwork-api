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
import org.spongepowered.asm.mixin.injection.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity {
	@Redirect(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
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
	 * Replaces the constant {@code false} in {@code this.removed = false;} with the current value of {@code this.removed}.
	 * This nullifies the action of this line, and allows us to control the revival instead with an inject.
	 */
	@ModifyConstant(method = "moveToWorld",
			slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;removePlayer(Lnet/minecraft/server/network/ServerPlayerEntity;)V", ordinal = 1),
					to = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getTeleportTarget(Lnet/minecraft/server/world/ServerWorld;)Lnet/minecraft/world/TeleportTarget;", ordinal = 0)),
			constant = @Constant(intValue = 0))
	private int nullifyMoveRemovedAssignment(int constant) {
		return ((Entity) (Object) this).removed ? 1 : 0;
	}

	/**
	 * Handles reviving the entity, as an alternative to the nullified {@code this.removed = false;}. By default, this
	 * will just run {@code this.removed = false;} anyways, but forge-added entities can provide an alternative implementation.
	 */
	@Inject(method = "moveToWorld",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getTeleportTarget(Lnet/minecraft/server/world/ServerWorld;)Lnet/minecraft/world/TeleportTarget;"))
	private void onMoveReviveEntity(CallbackInfoReturnable<Entity> ci) {
		((IForgeEntity) this).revive();
	}

	/**
	 * Replaces the constant {@code false} in {@code this.removed = false;} with the current value of {@code this.removed}.
	 * This nullifies the action of this line, and allows us to control the revival instead with an inject.
	 */
	@ModifyConstant(method = "teleport",
			slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;removePlayer(Lnet/minecraft/server/network/ServerPlayerEntity;)V"),
					to = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;refreshPositionAndAngles(DDDFF)V")),
			constant = @Constant(intValue = 0))
	private int nullifyTeleportRemovedAssignment(int constant) {
		return ((Entity) (Object) this).removed ? 1 : 0;
	}

	/**
	 * Handles reviving the entity, as an alternative to the nullified {@code this.removed = false;}. By default, this
	 * will just run {@code this.removed = false;} anyways, but forge-added entities can provide an alternative implementation.
	 */
	@Inject(method = "teleport",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;refreshPositionAndAngles(DDDFF)V"))
	private void onTeleportReviveEntity(CallbackInfo ci) {
		((IForgeEntity) this).revive();
	}
}
