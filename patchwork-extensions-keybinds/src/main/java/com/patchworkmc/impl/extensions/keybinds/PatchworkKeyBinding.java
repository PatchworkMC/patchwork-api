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

package com.patchworkmc.impl.extensions.keybinds;

import net.minecraftforge.client.extensions.IForgeKeybinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

/**
 * Keybindings with extra constructors, patchwork patcher should redirect classes using forge constructors here.
 */
public class PatchworkKeyBinding extends KeyBinding {
	public PatchworkKeyBinding(String id, int keyCode, String category) {
		super(id, keyCode, category);
	}

	public PatchworkKeyBinding(String id, InputUtil.Type type, int code, String category) {
		super(id, type, code, category);
	}

	/**
	 * Convenience constructor for creating KeyBindings with keyConflictContext set.
	 */
	public PatchworkKeyBinding(String description, IKeyConflictContext keyConflictContext, final InputUtil.Type inputType, final int keyCode, String category) {
		this(description, keyConflictContext, inputType.createFromCode(keyCode), category);
	}

	/**
	 * Convenience constructor for creating KeyBindings with keyConflictContext set.
	 */
	public PatchworkKeyBinding(String description, IKeyConflictContext keyConflictContext, InputUtil.KeyCode keyCode, String category) {
		this(description, keyConflictContext, KeyModifier.NONE, keyCode, category);
	}

	/**
	 * Convenience constructor for creating KeyBindings with keyConflictContext and keyModifier set.
	 */
	public PatchworkKeyBinding(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, final InputUtil.Type inputType, final int keyCode, String category) {
		this(description, keyConflictContext, keyModifier, inputType.createFromCode(keyCode), category);
	}

	/**
	 * Convenience constructor for creating KeyBindings with keyConflictContext and keyModifier set.
	 */
	public PatchworkKeyBinding(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, InputUtil.KeyCode keyCode, String category) {
		this(description, keyCode.getCategory(), keyCode.getKeyCode(), category);
		((IForgeKeybinding) this).setKeyConflictContext(keyConflictContext);
		((IPatchworkKeyBinding) this).setKeyModifier(keyModifier);
		((IPatchworkKeyBinding) this).setKeyModifierDefault(keyModifier);

		if (((IForgeKeybinding) this).getKeyModifier().matches(keyCode)) {
			((IPatchworkKeyBinding) this).setKeyModifier(KeyModifier.NONE);
		}
	}
}
