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

import com.mojang.authlib.GameProfile;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;

import net.patchworkmc.impl.event.entity.EntityEventsOld;
import net.patchworkmc.impl.event.entity.PlayerEvents;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity {
	public MixinServerPlayerEntity(World world, GameProfile profile) {
		super(world, profile);
	}

	@Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
	private void hookDeath(DamageSource source, CallbackInfo callback) {
		LivingEntity entity = (LivingEntity) (Object) this;

		if (EntityEventsOld.onLivingDeath(entity, source)) {
			callback.cancel();
		}
	}

	@Inject(method = "copyFrom", at = @At("TAIL"))
	private void hookCopyFromForCloneEvent(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo info) {
		@SuppressWarnings("ConstantConditions")
		ServerPlayerEntity speThis = (ServerPlayerEntity) (Object) this;
		MinecraftForge.EVENT_BUS.post(new PlayerEvent.Clone(speThis, oldPlayer, !alive));
	}

	@Inject(method = "teleport",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/network/ServerPlayerEntity;getServerWorld()Lnet/minecraft/server/world/ServerWorld;"
			),
			cancellable = true
	)
	void patchwork_fireTravelToDimensionEventTeleport(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo ci) {
		if (!EntityEventsOld.onTravelToDimension(this, targetWorld.dimension.getType())) {
			ci.cancel();
		}
	}

	////////////////////////////////////
	// PlayerChangedDimensionEvent
	////////////////////////////////////
	@Inject(method = "teleport",
			at = @At(
					value = "INVOKE",
					shift = Shift.AFTER,
					ordinal = 0,
					target = "net/minecraft/server/PlayerManager.method_14594(Lnet/minecraft/server/network/ServerPlayerEntity;)V"
					),
			locals = LocalCapture.CAPTURE_FAILHARD
	) // PlayerManager.method_14594 -> sendInventory
	private void teleport_sendInventory(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo info, ServerWorld serverWorld, LevelProperties levelProperties) {
		ServerPlayerEntity me = (ServerPlayerEntity) (Object) this;
		PlayerEvents.firePlayerChangedDimensionEvent(me, serverWorld.dimension.getType(), me.dimension);
	}

	@Unique
	private static final ThreadLocal<DimensionType> changeDimension_from = new ThreadLocal<>();

	@Inject(method = "changeDimension",
			at = @At("HEAD"),
			cancellable = true
	)
	void patchwork_fireTravelToDimensionEventChangeDimensionPlayer(DimensionType newDimension, CallbackInfoReturnable<Entity> cir) {
		if (!EntityEventsOld.onTravelToDimension(this, newDimension)) {
			cir.setReturnValue(null);
		}
	}

	@Inject(method = "changeDimension",
			at = @At(
					value = "INVOKE",
					shift = Shift.AFTER,
					ordinal = 0,
					target = "net/minecraft/server/MinecraftServer.getWorld(Lnet/minecraft/world/dimension/DimensionType;)Lnet/minecraft/server/world/ServerWorld;"
					)
	)
	private void changeDimension_getWorld(DimensionType newDimension, CallbackInfoReturnable<Entity> info) {
		ServerPlayerEntity me = (ServerPlayerEntity) (Object) this;
		changeDimension_from.set(me.dimension);
	}

	@Inject(method = "changeDimension",
			at = @At(
					value = "FIELD",
					shift = Shift.AFTER,
					ordinal = 0,
					target = "net/minecraft/server/network/ServerPlayerEntity.field_13979:I"
					)
	) // ServerPlayerEntity.field_13979 -> lastFoodLevel
	private void changeDimension_lastFoodLevel(DimensionType newDimension, CallbackInfoReturnable<Entity> info) {
		ServerPlayerEntity me = (ServerPlayerEntity) (Object) this;
		DimensionType from = changeDimension_from.get();
		changeDimension_from.set(null);
		PlayerEvents.firePlayerChangedDimensionEvent(me, from, newDimension);
	}
}
