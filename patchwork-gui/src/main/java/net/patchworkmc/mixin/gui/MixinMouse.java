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

package net.patchworkmc.mixin.gui;

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
import net.minecraft.client.gui.screen.Screen;

import net.patchworkmc.impl.gui.ForgeMouse;

@Mixin(Mouse.class)
public abstract class MixinMouse implements ForgeMouse {
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
	public void preMouseClicked(boolean[] handled, double mouseX, double mouseY, int button, CallbackInfo info) {
		if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseClickedEvent.Pre(client.currentScreen, mouseX, mouseY, button))) {
			handled[0] = true;
			info.cancel();
		}
	}

	@Inject(method = "method_1611", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void postMouseClicked(boolean[] handled, double mouseX, double mouseY, int button, CallbackInfo info) {
		if (handled[0]) {
			return;
		}

		if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseClickedEvent.Post(client.currentScreen, mouseX, mouseY, button))) {
			handled[0] = true;
			info.cancel();
		}
	}

	@Inject(method = "method_1605", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void preMouseReleased(boolean[] handled, double mouseX, double mouseY, int button, CallbackInfo info) {
		if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseReleasedEvent.Pre(client.currentScreen, mouseX, mouseY, button))) {
			handled[0] = true;
			info.cancel();
		}
	}

	@Inject(method = "method_1605", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void postMouseReleased(boolean[] handled, double mouseX, double mouseY, int button, CallbackInfo info) {
		if (handled[0]) {
			return;
		}

		if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseReleasedEvent.Post(client.currentScreen, mouseX, mouseY, button))) {
			handled[0] = true;
			info.cancel();
		}
	}

	@Inject(method = "method_1602", at = @At("HEAD"), cancellable = true)
	private void preMouseDragged(Element element, double mouseX, double mouseY, double deltaX, double deltaY, CallbackInfo info) {
		if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseDragEvent.Pre((Screen) element, mouseX, mouseY, activeButton, deltaX, deltaY))) {
			info.cancel();
		}
	}

	@Inject(method = "method_1602", at = @At("RETURN"))
	private void postMouseDragged(Element element, double mouseX, double mouseY, double deltaX, double deltaY, CallbackInfo info) {
		MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseDragEvent.Post((Screen) element, mouseX, mouseY, activeButton, deltaX, deltaY));
	}

	@Inject(method = "onMouseScroll", at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDD)Z",
					ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void preMouseScrolled(long window, double xOffset, double yOffset, CallbackInfo info, double amount, double mouseX, double mouseY) {
		if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseScrollEvent.Pre(client.currentScreen, mouseX, mouseY, amount))) {
			info.cancel();
		}
	}

	@Inject(method = "onMouseScroll", at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDD)Z",
					ordinal = 0, shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void postMouseScrolled(long window, double xOffset, double yOffset, CallbackInfo info, double amount, double mouseX, double mouseY) {
		MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseScrollEvent.Post(client.currentScreen, mouseX, mouseY, amount));
	}

	@Override
	public boolean isMiddleDown() {
		return this.middleButtonClicked;
	}

	@Override
	public double getXVelocity() {
		return this.cursorDeltaX;
	}

	@Override
	public double getYVelocity() {
		return this.cursorDeltaY;
	}
}
