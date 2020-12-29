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

package net.patchworkmc.mixin.keybindings;

import net.minecraftforge.client.extensions.IForgeKeybinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

import net.patchworkmc.impl.keybindings.ForgeKeyBindingConstruct;

@Mixin(KeyBinding.class)
public abstract class MixinKeyBinding implements Comparable<KeyBinding>, IForgeKeybinding, ForgeKeyBindingConstruct {
	@Shadow
	private InputUtil.Key boundKey;

	// These exist in Forge to allow modifiers and conflicting keys to work, this is not implemented
	// but these remain to avoid stubbing the methods
	@Unique
	private KeyModifier keyModifierDefault = KeyModifier.NONE;
	@Unique
	private KeyModifier keyModifier = KeyModifier.NONE;
	@Unique
	private IKeyConflictContext keyConflictContext = KeyConflictContext.UNIVERSAL;

	@Inject(method = "isPressed", at = @At("HEAD"))
	public void isPressed(CallbackInfoReturnable<Boolean> info) {
		if (!getKeyConflictContext().isActive()) {
			info.setReturnValue(false);
			info.cancel();
		}
	}

	@Override
	public InputUtil.Key getKey() {
		return this.boundKey;
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
	public void setKeyModifierAndCode(KeyModifier keyModifier, InputUtil.Key boundKey) {
		this.boundKey = boundKey;

		if (keyModifier.matches(boundKey)) {
			keyModifier = KeyModifier.NONE;
		}

		this.keyModifier = keyModifier;
		KeyBinding.updateKeysByCode();
	}

	@Override
	public void patchwork$constructForgeKeyBindingOptions(IKeyConflictContext keyConflictContext, KeyModifier keyModifier) {
		this.keyModifier = keyModifier;
		this.keyModifierDefault = keyModifier;
		this.keyConflictContext = keyConflictContext;
	}
}
