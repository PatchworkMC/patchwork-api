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
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.Screen;

@Mixin(Keyboard.class)
public abstract class MixinKeyboard {
	@Shadow
	private boolean repeatEvents;

	@Dynamic("lambda in onKey")
	@Inject(method = "method_1454", at = @At("HEAD"), cancellable = true)
	private void preKeyEvent(int i, boolean[] bls, ParentElement element, int key, int scanCode, int mods, CallbackInfo info) {
		if (i != 1 && (i != 2 || !this.repeatEvents)) {
			if (i == 0) {
				if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.KeyboardKeyReleasedEvent.Pre((Screen) element, key, scanCode, mods))) {
					bls[0] = true;
					info.cancel();
				}
			}
		} else {
			if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.KeyboardKeyPressedEvent.Pre((Screen) element, key, scanCode, mods))) {
				bls[0] = true;
				info.cancel();
			}
		}
	}

	@Dynamic("lambda in onKey")
	@Inject(method = "method_1454", at = @At("TAIL"))
	private void postKeyEvent(int i, boolean[] bls, ParentElement element, int key, int scanCode, int mods, CallbackInfo info) {
		if (bls[0]) {
			return;
		}

		if (i != 1 && (i != 2 || !this.repeatEvents)) {
			if (i == 0) {
				if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.KeyboardKeyReleasedEvent.Post((Screen) element, key, scanCode, mods))) {
					bls[0] = true;
				}
			}
		} else {
			if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.KeyboardKeyPressedEvent.Post((Screen) element, key, scanCode, mods))) {
				bls[0] = true;
			}
		}
	}

	@Dynamic("lambda in onChar")
	@Inject(method = "method_1458", at = @At("HEAD"), cancellable = true)
	private static void preCharTyped(Element element, int character, int mods, CallbackInfo info) {
		if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.KeyboardCharTypedEvent.Pre((Screen) element, (char) character, mods))) {
			info.cancel();
		}
	}

	@Dynamic("lambda in onChar")
	@Redirect(method = "method_1458(Lnet/minecraft/client/gui/Element;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Element;charTyped(CI)Z", ordinal = 0))
	private static boolean charTyped(Element element, char character, int mods) {
		return element.charTyped(character, mods) || MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.KeyboardCharTypedEvent.Post((Screen) element, character, mods));
	}

	@Dynamic("lambda in onChar")
	@Inject(method = "method_1473", at = @At("HEAD"), cancellable = true)
	private static void preCharTyped(Element element, char character, int mods, CallbackInfo info) {
		if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.KeyboardCharTypedEvent.Pre((Screen) element, character, mods))) {
			info.cancel();
		}
	}

	@Dynamic("lambda in onChar")
	@Redirect(method = "method_1473(Lnet/minecraft/client/gui/Element;CI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Element;charTyped(CI)Z", ordinal = 0))
	private static boolean charTyped2(Element element, char character, int mods) {
		return element.charTyped(character, mods) || MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.KeyboardCharTypedEvent.Post((Screen) element, character, mods));
	}
}
