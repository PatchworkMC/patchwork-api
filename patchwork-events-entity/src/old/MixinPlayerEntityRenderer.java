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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;

@Mixin(PlayerEntityRenderer.class)
public class MixinPlayerEntityRenderer {
	@Inject(method = "render", at = @At(value = "INVOKE", ordinal = 0, shift = Shift.BEFORE,
			target = "net/minecraft/client/network/AbstractClientPlayerEntity.isInSneakingPose()Z"), cancellable = true)
	private void preRender(AbstractClientPlayerEntity playerEntity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
		PlayerEntityRenderer me = (PlayerEntityRenderer) (Object) this;

		if (MinecraftForge.EVENT_BUS.post(new RenderPlayerEvent.Pre(playerEntity, me, partialTicks, x, y, z))) {
			ci.cancel();
		}
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void postRender(AbstractClientPlayerEntity playerEntity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
		PlayerEntityRenderer me = (PlayerEntityRenderer) (Object) this;
		MinecraftForge.EVENT_BUS.post(new RenderPlayerEvent.Post(playerEntity, me, partialTicks, x, y, z));
	}
}
