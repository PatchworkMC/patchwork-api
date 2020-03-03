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

import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraftforge.client.extensions.IForgeKeybinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;

import com.patchworkmc.impl.extensions.keybinds.IPatchworkKeyBinding;

@Mixin(KeyBinding.class)
public abstract class MixinKeyBinding implements IForgeKeybinding, IPatchworkKeyBinding {
	@Shadow
	private InputUtil.KeyCode keyCode;

	@Mutable
	@Shadow
	@Final
	private static Map<InputUtil.KeyCode, KeyBinding> keysByCode;

	@Shadow
	public abstract String getCategory();

	@Shadow
	public abstract String getId();

	@Shadow
	@Final
	private static Map<String, Integer> categoryOrderMap;
	private KeyModifier keyModifierDefault = KeyModifier.NONE;
	private KeyModifier keyModifier = KeyModifier.NONE;
	private IKeyConflictContext keyConflictContext = KeyConflictContext.UNIVERSAL;

	@Inject(method = "<clinit>", at = @At("RETURN"))
	private static void staticInit(CallbackInfo ci) {
		keysByCode = new KeyBindingMap();
	}

	@Inject(method = "onKeyPressed", at = @At("HEAD"), cancellable = true)
	private static void onKeyPressed(InputUtil.KeyCode keyCode, CallbackInfo ci) {
		ci.cancel();

		for (KeyBinding keybinding : ((KeyBindingMap) keysByCode).lookupAll(keyCode)) {
			if (keybinding != null) {
				((KeyBindingHooks) keybinding).setTimesPressed(((KeyBindingHooks) keybinding).getTimesPressed() + 1);
			}
		}
	}

	@Inject(method = "setKeyPressed", at = @At("HEAD"), cancellable = true)
	private static void onKeyPressed(InputUtil.KeyCode keyCode, boolean pressed, CallbackInfo ci) {
		ci.cancel();

		for (KeyBinding keybinding : ((KeyBindingMap) keysByCode).lookupAll(keyCode)) {
			if (keybinding != null) {
				((KeyBindingHooks) keybinding).setPressed(pressed);
			}
		}
	}

	@Inject(method = "isPressed", at = @At("RETURN"), cancellable = true)
	private void isPressed(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(cir.getReturnValueZ() && getKeyConflictContext().isActive() && getKeyModifier().isActive(getKeyConflictContext()));
	}

	@Inject(method = "method_1430", at = @At("HEAD"), cancellable = true)
	private void compareTo(KeyBinding keyBinding, CallbackInfoReturnable<Integer> cir) {
		if (this.getCategory().equals(keyBinding.getCategory())) {
			cir.setReturnValue(I18n.translate(this.getId()).compareTo(I18n.translate(keyBinding.getId())));
			return;
		}

		Integer tCat = categoryOrderMap.get(this.getCategory());
		Integer oCat = categoryOrderMap.get(keyBinding.getCategory());

		if (tCat == null && oCat != null) {
			cir.setReturnValue(1);
			return;
		}

		if (tCat != null && oCat == null) {
			cir.setReturnValue(-1);
			return;
		}

		if (tCat == null && oCat == null) {
			cir.setReturnValue(I18n.translate(this.getCategory()).compareTo(I18n.translate(keyBinding.getCategory())));
			return;
		}

		cir.setReturnValue(tCat.compareTo(oCat));
	}

	@Inject(method = "equals", at = @At("HEAD"), cancellable = true)
	private void equals(KeyBinding binding, CallbackInfoReturnable<Boolean> cir) {
		if (getKeyConflictContext().conflicts(((IForgeKeybinding) binding).getKeyConflictContext()) || ((IForgeKeybinding) binding).getKeyConflictContext().conflicts(getKeyConflictContext())) {
			KeyModifier keyModifier = getKeyModifier();
			KeyModifier otherKeyModifier = ((IForgeKeybinding) binding).getKeyModifier();

			if (keyModifier.matches(((IForgeKeybinding) binding).getKey()) || otherKeyModifier.matches(getKey())) {
				cir.setReturnValue(true);
			} else if (getKey().equals(((IForgeKeybinding) binding).getKey())) {
				// IN_GAME key contexts have a conflict when at least one modifier is NONE.
				// For example: If you hold shift to crouch, you can still press E to open your inventory. This means that a Shift+E hotkey is in conflict with E.
				// GUI and other key contexts do not have this limitation.
				cir.setReturnValue(keyModifier == otherKeyModifier
								|| (getKeyConflictContext().conflicts(KeyConflictContext.IN_GAME)
								&& (keyModifier == KeyModifier.NONE || otherKeyModifier == KeyModifier.NONE)));
			}
		}
	}

	@Inject(method = "getLocalizedName()Ljava/lang/String;", at = @At("RETURN"), cancellable = true)
	private void getLocalizedName(CallbackInfoReturnable<String> cir) {
		cir.setReturnValue(getKeyModifier().getLocalizedComboName(keyCode, cir::getReturnValue));
	}

	@Inject(method = "isDefault", at = @At("RETURN"), cancellable = true)
	private void isDefault(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(cir.getReturnValueZ() && getKeyModifier() == getKeyModifierDefault());
	}

	@Nonnull
	@Override
	public InputUtil.KeyCode getKey() {
		return this.keyCode;
	}

	@Override
	public IKeyConflictContext getKeyConflictContext() {
		return keyConflictContext;
	}

	@Override
	public void setKeyConflictContext(IKeyConflictContext keyConflictContext) {
		this.keyConflictContext = keyConflictContext;
	}

	@Override
	public KeyModifier getKeyModifierDefault() {
		return keyModifierDefault;
	}

	@Override
	public KeyModifier getKeyModifier() {
		return keyModifier;
	}

	@Override
	public void setKeyModifierAndCode(KeyModifier keyModifier, InputUtil.KeyCode keyCode) {
		this.keyCode = keyCode;

		if (keyModifier.matches(keyCode)) {
			keyModifier = KeyModifier.NONE;
		}

		((KeyBindingMap) keysByCode).removeKey((KeyBinding) (Object) this);
		this.keyModifier = keyModifier;
		((KeyBindingMap) keysByCode).addKey(keyCode, (KeyBinding) (Object) this);
	}

	@Override
	public void setKeyModifier(KeyModifier keyModifier) {
		this.keyModifier = keyModifier;
	}

	@Override
	public void setKeyModifierDefault(KeyModifier keyModifierDefault) {
		this.keyModifierDefault = keyModifierDefault;
	}
}
