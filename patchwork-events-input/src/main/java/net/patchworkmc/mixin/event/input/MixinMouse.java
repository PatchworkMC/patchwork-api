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

package net.patchworkmc.mixin.event.input;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.Mouse;

import net.patchworkmc.api.input.ForgeMouse;
import net.patchworkmc.impl.event.input.InputEvents;

@Mixin(Mouse.class)
public abstract class MixinMouse implements ForgeMouse {
	// We want to target all returns here in order for the event to fire correctly.
	@Inject(method = "onMouseButton", at = @At("RETURN"))
	private void fireMouseInput(long window, int button, int action, int mods, CallbackInfo info) {
		InputEvents.fireMouseInput(button, action, mods);
	}

	@Inject(method = "onMouseButton", at = @At(value = "FIELD", ordinal = 3, target = "Lnet/minecraft/client/Mouse;client:Lnet/minecraft/client/MinecraftClient;", shift = Shift.BEFORE), cancellable = true)
	private void onRawMouseClicked(long window, int button, int action, int mods, CallbackInfo info) {
		if (InputEvents.onRawMouseClicked(button, action, mods)) {
			info.cancel();
		}
	}

	@Inject(method = "onMouseScroll", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z", shift = Shift.BEFORE), cancellable = true)
	private void onMouseScroll(long window, double d, double e, CallbackInfo info, double scrollDelta, float i) {
		if (InputEvents.onMouseScroll((Mouse) (Object) this, scrollDelta)) {
			info.cancel();
		}
	}

	// Methods added by Forge
	@Shadow
	private boolean middleButtonClicked;

	@Shadow
	private double cursorDeltaX;

	@Shadow
	private double cursorDeltaY;

	@Override
	public boolean isMiddleDown() {
		return middleButtonClicked;
	}

	@Override
	public double getXVelocity() {
		return cursorDeltaX;
	}

	@Override
	public double getYVelocity() {
		return cursorDeltaY;
	}
}
