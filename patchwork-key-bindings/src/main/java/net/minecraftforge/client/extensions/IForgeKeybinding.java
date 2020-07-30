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

package net.minecraftforge.client.extensions;

import javax.annotation.Nonnull;

import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

public interface IForgeKeybinding {
	default KeyBinding getKeyBinding() {
		return (KeyBinding) this;
	}

	@Nonnull
	InputUtil.KeyCode getKey();

	/**
	 * Checks that the key conflict context and modifier are active, and that the keyCode matches this binding.
	 */
	default boolean isActiveAndMatches(InputUtil.KeyCode keyCode) {
		return keyCode.getKeyCode() != 0 && keyCode.equals(getKey()) && getKeyConflictContext().isActive() && getKeyModifier().isActive(getKeyConflictContext());
	}

	default void setToDefault() {
		setKeyModifierAndCode(getKeyModifierDefault(), getKeyBinding().getDefaultKeyCode());
	}

	IKeyConflictContext getKeyConflictContext();

	void setKeyConflictContext(IKeyConflictContext keyConflictContext);

	KeyModifier getKeyModifierDefault();

	KeyModifier getKeyModifier();

	void setKeyModifierAndCode(KeyModifier keyModifier, InputUtil.KeyCode keyCode);

	/**
	 * Returns true when one of the bindings' key codes conflicts with the other's modifier.
	 */
	default boolean hasKeyCodeModifierConflict(KeyBinding other) {
		if (getKeyConflictContext().conflicts(((IForgeKeybinding) other).getKeyConflictContext()) || ((IForgeKeybinding) other).getKeyConflictContext().conflicts(getKeyConflictContext())) {
			return getKeyModifier().matches(((IForgeKeybinding) other).getKey()) || ((IForgeKeybinding) other).getKeyModifier().matches(getKey());
		}

		return false;
	}
}
