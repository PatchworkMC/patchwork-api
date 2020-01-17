/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2019, 2019
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

package com.patchworkmc.mixin.rendering;

import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.Element;

@Mixin(Mouse.class)
public abstract class MixinMouse {
	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	private int activeButton;

	@Shadow
	private boolean middleButtonClicked;

	@Shadow
	private double cursorDeltaY;

	@Shadow
	private double cursorDeltaX;

	@Inject(method = "method_1611", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void preMouseClicked(boolean[] bls, double d, double e, int button, CallbackInfo info) {
		if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseClickedEvent.Pre(client.currentScreen, d, e, button))) {
			bls[0] = true;
			info.cancel();
		}
	}

	@Inject(method = "method_1611", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void postMouseClicked(boolean[] bls, double d, double e, int button, CallbackInfo info) {
		if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseClickedEvent.Post(client.currentScreen, d, e, button))) {
			bls[0] = true;
			info.cancel();
		}
	}

	@Inject(method = "method_1605", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void preMouseReleased(boolean[] bls, double d, double e, int button, CallbackInfo info) {
		if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseReleasedEvent.Pre(client.currentScreen, d, e, button))) {
			bls[0] = true;
			info.cancel();
		}
	}

	@Inject(method = "method_1605", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void postMouseReleased(boolean[] bls, double d, double e, int button, CallbackInfo info) {
		if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseReleasedEvent.Post(client.currentScreen, d, e, button))) {
			bls[0] = true;
			info.cancel();
		}
	}

	@Inject(method = "method_1602", at = @At("HEAD"), cancellable = true)
	public void preMouseDragged(Element element, double d, double e, double f, double g, CallbackInfo info) {
		if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseDragEvent.Pre(client.currentScreen, d, e, activeButton, f, g))) {
			info.cancel();
		}
	}

	@Inject(method = "method_1602", at = @At("RETURN"))
	public void postMouseDragged(Element element, double d, double e, double f, double g, CallbackInfo info) {
		MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseDragEvent.Post(client.currentScreen, d, e, activeButton, f, g));
	}

	@Inject(method = "onMouseScroll", at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDD)Z",
					ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void preMouseScrolled(long window, double d, double e, CallbackInfo ci, double f, double g, double h) {
		if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseScrollEvent.Pre(client.currentScreen, g, h, f))) {
			ci.cancel();
		}
	}

	@Inject(method = "onMouseScroll", at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDD)Z",
					ordinal = 0, shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void postMouseScrolled(long window, double d, double e, CallbackInfo ci, double f, double g, double h) {
		MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseScrollEvent.Post(client.currentScreen, g, h, f));
	}

	public boolean isMiddleDown() {
		return this.middleButtonClicked;
	}

	public double getXVelocity() {
		return this.cursorDeltaX;
	}

	public double getYVelocity() {
		return this.cursorDeltaY;
	}
}
