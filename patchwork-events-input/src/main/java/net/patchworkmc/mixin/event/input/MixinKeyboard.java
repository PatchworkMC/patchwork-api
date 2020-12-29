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

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;

import net.patchworkmc.impl.event.input.InputEvents;

@Mixin(Keyboard.class)
public abstract class MixinKeyboard {
	@Shadow
	@Final
	private MinecraftClient client;

	// We want to target all returns here to correctly fire the event when a key is pressed
	@Inject(method = "onKey", at = @At("RETURN"))
	private void fireKeyInput(long window, int key, int scancode, int action, int modifiers, CallbackInfo info) {
		if (window == this.client.getWindow().getHandle()) {
			InputEvents.fireKeyInput(key, scancode, action, modifiers);
		}
	}
}
