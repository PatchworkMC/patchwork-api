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

package net.patchworkmc.api.keybindings;

import net.minecraftforge.client.extensions.IForgeKeybinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.patchworkmc.impl.keybindings.ForgeKeyBindingConstruct;

public class PatchworkKeyBinding extends KeyBinding {
	public PatchworkKeyBinding(String id, int keyCode, String category) {
		super(id, keyCode, category);
		KeyBindingHelper.registerKeyBinding(this);
	}

	public PatchworkKeyBinding(String id, InputUtil.Type type, int code, String category) {
		super(id, type, code, category);
		KeyBindingHelper.registerKeyBinding(this);
	}

	public PatchworkKeyBinding(String id, IKeyConflictContext keyConflictContext, final InputUtil.Type inputType, final int keyCode, String category) {
		this(id, keyConflictContext, inputType.createFromCode(keyCode), category);
	}

	public PatchworkKeyBinding(String id, IKeyConflictContext keyConflictContext, InputUtil.Key keyCode, String category) {
		this(id, keyConflictContext, KeyModifier.NONE, keyCode, category);
	}

	public PatchworkKeyBinding(String id, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, final InputUtil.Type inputType, final int keyCode, String category) {
		this(id, keyConflictContext, keyModifier, inputType.createFromCode(keyCode), category);
	}

	public PatchworkKeyBinding(String id, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, InputUtil.Key keyCode, String category) {
		super(id, keyCode.getCategory(), keyCode.getCode(), category);
		((IForgeKeybinding) this).setKeyConflictContext(keyConflictContext);
		((IForgeKeybinding) this).setKeyModifierAndCode(keyModifier, keyCode);
		((ForgeKeyBindingConstruct) this).patchwork$constructForgeKeyBindingOptions(keyConflictContext, keyModifier.matches(keyCode) ? KeyModifier.NONE : keyModifier);
		KeyBindingHelper.registerKeyBinding(this);
	}
}
