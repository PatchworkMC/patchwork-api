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

package net.patchworkmc.mixin.gui.hud;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.ALL;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.net.minecraftforge.client.ForgeIngameGui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.LivingEntity;

import net.patchworkmc.impl.gui.PatchworkGui;

@Mixin(InGameHud.class)
public class HookMainEvent {
	@Shadow
	@Final
	private MinecraftClient client;
	@Unique
	private boolean mainCanceled = false;

	// Technically this hook happens a tiny bit later, but it shouldn't make a difference.
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void preMainEvent(float tickDelta, CallbackInfo info) {
		PatchworkGui.eventParent = new RenderGameOverlayEvent(tickDelta, this.client.window);
		ForgeIngameGui.renderHealthMount = client.player.getVehicle() instanceof LivingEntity;
		ForgeIngameGui.renderFood = client.player.getVehicle() == null;
		// ForgeIngameGui.renderJumpBar = client.player.isRiding();
		ForgeIngameGui.left_height = 39;
		ForgeIngameGui.right_height = 39;

		if (PatchworkGui.pre(ALL)) {
			mainCanceled = true;
			info.cancel();
		}
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void postMainEvent(float tickDelta, CallbackInfo info) {
		PatchworkGui.post(ALL);

		mainCanceled = false;
	}
}
