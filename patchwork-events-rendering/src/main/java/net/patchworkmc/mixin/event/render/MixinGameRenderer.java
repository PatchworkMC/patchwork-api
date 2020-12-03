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

package net.patchworkmc.mixin.event.render;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.hit.HitResult;

import net.patchworkmc.impl.event.render.RenderEvents;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
	@Shadow
	@Final
	private MinecraftClient client;

	@Redirect(
			method = "renderCenter", at = @At(value = "INVOKE", ordinal = 0,
			target = "net/minecraft/client/render/WorldRenderer.drawHighlightedBlockOutline(Lnet/minecraft/client/render/Camera;Lnet/minecraft/util/hit/HitResult;I)V"
			)
	)
	private void patchwork_renderCenter_drawHighlightedBlockOutline(WorldRenderer worldRenderer, Camera camera, HitResult hit, int renderPass, float partialTicks, long nanoTime) {
		MinecraftClient client = ((GameRenderer) (Object) this).getClient();

		if (!RenderEvents.onDrawHighlightEvent(worldRenderer, camera, hit, 0, partialTicks)) {
			worldRenderer.drawHighlightedBlockOutline(camera, client.crosshairTarget, 0);
		}
	}

	@Inject(method = "renderCenter", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 15), locals = LocalCapture.CAPTURE_FAILHARD)
	private void hookRenderWorldLastEvent(float tickDelta, long endTime, CallbackInfo ci, WorldRenderer context) {
		this.client.getProfiler().swap("forge_render_last");
		RenderEvents.onRenderLast(context, tickDelta);
	}
}
