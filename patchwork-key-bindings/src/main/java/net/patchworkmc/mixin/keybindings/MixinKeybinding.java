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

import java.util.Map;
import java.util.Set;

import net.minecraftforge.client.extensions.IForgeKeybinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.patchworkmc.impl.keybindings.KeyBindingTypeChecker;

@Mixin(KeyBinding.class)
public abstract class MixinKeybinding implements Comparable<KeyBinding>, IForgeKeybinding {
	@Shadow
	private static Map<String, KeyBinding> keysById;
	@Shadow
	private static Map<InputUtil.KeyCode, KeyBinding> keysByCode;
	@Shadow
	private static Set<String> keyCategories;
	@Shadow
	private String id;
	@Shadow
	private InputUtil.KeyCode defaultKeyCode;
	@Shadow
	private String category;
	@Shadow
	private InputUtil.KeyCode keyCode;

	//These exist in forge to allow modifiers and conflicting keys to work, this is not currently implemented
	//but these remain to avoid having to stub the methods or break mods that reflect into these for some reason
	private KeyModifier keyModifierDefault = KeyModifier.NONE;
	private KeyModifier keyModifier = KeyModifier.NONE;
	private IKeyConflictContext keyConflictContext = KeyConflictContext.UNIVERSAL;

	@Inject(method = "<init>(Ljava/lang/String;Lnet/minecraft/client/util/InputUtil$Type;ILjava/lang/String;)V", at = @At("RETURN"))
	public void init(String id, InputUtil.Type type, int code, String category, CallbackInfo info) {
		if (KeyBindingTypeChecker.isModded()) {
			KeyBindingHelper.registerKeyBinding((KeyBinding) (Object) this);
		}
	}

	public MixinKeybinding(String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, final InputUtil.Type inputType, final int keyCode, String category) {
		this(description, keyConflictContext, inputType.createFromCode(keyCode), category);
	}

	public MixinKeybinding(String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, InputUtil.KeyCode keyCode, String category) {
		this(description, keyConflictContext, net.minecraftforge.client.settings.KeyModifier.NONE, keyCode, category);
	}

	public MixinKeybinding(String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, net.minecraftforge.client.settings.KeyModifier keyModifier, final InputUtil.Type inputType, final int keyCode, String category) {
		this(description, keyConflictContext, keyModifier, inputType.createFromCode(keyCode), category);
	}

	public MixinKeybinding(String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, net.minecraftforge.client.settings.KeyModifier keyModifier, InputUtil.KeyCode keyCode, String category) {
		this.id = description;
		this.keyCode = keyCode;
		this.defaultKeyCode = keyCode;
		this.category = category;
		this.keyConflictContext = keyConflictContext;
		this.keyModifier = keyModifier;
		this.keyModifierDefault = keyModifier;

		if (this.keyModifier.matches(keyCode)) {
			this.keyModifier = net.minecraftforge.client.settings.KeyModifier.NONE;
		}

		keysById.put(description, (KeyBinding) (Object) this);
		keyCategories.add(category);

		if (KeyBindingTypeChecker.isModded()) {
			KeyBindingHelper.registerKeyBinding((KeyBinding) (Object) this);
		}
	}

	@Override
	public InputUtil.KeyCode getKey() {
		return this.keyCode;
	}

	@Override
	public net.minecraftforge.client.settings.IKeyConflictContext getKeyConflictContext() {
		return keyConflictContext;
	}

	@Override
	public void setKeyConflictContext(net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext) {
		this.keyConflictContext = keyConflictContext;
	}

	@Override
	public net.minecraftforge.client.settings.KeyModifier getKeyModifierDefault() {
		return keyModifierDefault;
	}

	@Override
	public net.minecraftforge.client.settings.KeyModifier getKeyModifier() {
		return keyModifier;
	}

	@Override
	public void setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier keyModifier, InputUtil.KeyCode keyCode) {
		this.keyCode = keyCode;

		if (keyModifier.matches(keyCode)) {
			keyModifier = net.minecraftforge.client.settings.KeyModifier.NONE;
		}

		this.keyModifier = keyModifier;
		KeyBinding.updateKeysByCode();
	}
}
