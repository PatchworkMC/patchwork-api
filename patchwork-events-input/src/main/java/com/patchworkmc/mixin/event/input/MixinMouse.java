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

package com.patchworkmc.mixin.event.input;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import net.minecraft.client.Mouse;

@Mixin(Mouse.class)
public abstract class MixinMouse {
	@Shadow
	boolean middleButtonClicked;
	@Shadow
	abstract boolean wasLeftButtonClicked();
	@Shadow
	abstract boolean wasRightButtonClicked();
	@Shadow
	abstract double getX();
	@Shadow
	abstract double getY();

	@Inject(method = "onMouseButton", at = @At("RETURN"), cancellable = true)
	private void fireMouseInput(long window, int button, int action, int mods, CallbackInfo info) {
		MinecraftForge.EVENT_BUS.post(new InputEvent.MouseInputEvent(button, action, mods));
	}

	@Inject(method = "onMouseButton", at = @At(value = "FIELD", ordinal = 3, target = "Lnet/minecraft/client/Mouse;client:Lnet/minecraft/client/MinecraftClient;", shift = Shift.BEFORE), cancellable = true)
	private void onRawMouseClicked(long window, int button, int action, int mods, CallbackInfo info) {
		if (MinecraftForge.EVENT_BUS.post(new InputEvent.RawMouseEvent(button, action, mods))) {
			info.cancel();
		}
	}

	@Inject(method = "onMouseScroll", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z", shift = Shift.BEFORE), cancellable = true)
	private void onMouseScroll(long window, double d, double e, CallbackInfo info, double f, float i) {
		final Event event = new InputEvent.MouseScrollEvent(f, wasLeftButtonClicked(), middleButtonClicked, wasRightButtonClicked(), getX(), getY());

		if (MinecraftForge.EVENT_BUS.post(event)) {
			info.cancel();
		}
	}
}
