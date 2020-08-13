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

package net.patchworkmc.mixin.networking.handler;

import net.minecraftforge.fml.network.ICustomPacket;
import net.minecraftforge.fml.network.NetworkHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.world.dimension.DimensionType;

import net.patchworkmc.impl.networking.ClientNetworkingEvents;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {
	@Shadow
	private MinecraftClient client;

	@Shadow
	public abstract ClientConnection getConnection();

	@Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
	private void patchwork$hookCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo callback) {
		if (NetworkHooks.onCustomPayload((ICustomPacket<?>) packet, getConnection())) {
			callback.cancel();
		}
	}

	@Inject(method = "onGameJoin", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/GameJoinS2CPacket;getEntityId()I"))
	public void patchwork$hookGameJoin(CallbackInfo ci) {
		ClientNetworkingEvents.firePlayerLogin(this.client.interactionManager, this.client.player, this.client.getNetworkHandler().getConnection());
	}

	@Inject(method = "onPlayerRespawn",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;addPlayer(ILnet/minecraft/client/network/AbstractClientPlayerEntity;)V"),
			locals = LocalCapture.CAPTURE_FAILHARD)
	public void patchwork$hookPlayerRespawn(PlayerRespawnS2CPacket packet, CallbackInfo ci, DimensionType dimensionType, ClientPlayerEntity clientPlayerEntity, int i, String string, ClientPlayerEntity clientPlayerEntity2) {
		ClientNetworkingEvents.firePlayerRespawn(this.client.interactionManager, clientPlayerEntity, clientPlayerEntity2, clientPlayerEntity2.networkHandler.getConnection());
	}
}
