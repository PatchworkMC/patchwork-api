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

package com.patchworkmc.mixin.extensions.keybinds;

import net.minecraftforge.client.extensions.IForgeKeybinding;
import net.minecraftforge.client.settings.KeyModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.gui.screen.controls.ControlsOptionsScreen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

@Mixin(ControlsOptionsScreen.class)
public class MixinControlsOptionsScreen {
	@Shadow
	public KeyBinding focusedBinding;

	@Unique
	public KeyBinding focusedTempBinding;

	@Redirect(method = "method_19872", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/options/KeyBinding;setKeyCode(Lnet/minecraft/client/util/InputUtil$KeyCode;)V"))
	private void resetToDefault(KeyBinding keyBinding, InputUtil.KeyCode keyCode) {
		((IForgeKeybinding) keyBinding).setToDefault();
	}

	@Inject(method = "keyPressed", at = @At("HEAD"))
	private void setKeyCodeHead(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		focusedTempBinding = focusedBinding;
	}

	@Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/options/GameOptions;setKeyCode(Lnet/minecraft/client/options/KeyBinding;Lnet/minecraft/client/util/InputUtil$KeyCode;)V", ordinal = 0))
	private void setKeyCodeOne(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		((IForgeKeybinding) this.focusedBinding).setKeyModifierAndCode(KeyModifier.getActiveModifier(), InputUtil.UNKNOWN_KEYCODE);
	}

	@Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/options/GameOptions;setKeyCode(Lnet/minecraft/client/options/KeyBinding;Lnet/minecraft/client/util/InputUtil$KeyCode;)V", ordinal = 1))
	private void setKeyCodeTwo(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		((IForgeKeybinding) this.focusedBinding).setKeyModifierAndCode(KeyModifier.getActiveModifier(), InputUtil.getKeyCode(keyCode, scanCode));
	}

	@Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/SystemUtil;getMeasuringTimeMs()J"))
	private void setKeyCodeReturn(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		if (focusedTempBinding != null) {
			if (KeyModifier.isKeyCodeModifier(((IForgeKeybinding) this.focusedTempBinding).getKey())) {
				focusedBinding = focusedTempBinding;
			}

			focusedTempBinding = null;
		}
	}
}
