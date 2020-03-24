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

package net.patchworkmc.impl.registries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.options.KeyBinding;

public class AddedKeybinds {
	private static Set<KeyBinding> registeredKeys = new HashSet<>();

	public static KeyBinding[] addRegisteredKeys(KeyBinding[] keysAll) {
		List<KeyBinding> newKeysAll = new ArrayList<>();

		for (KeyBinding binding : keysAll) {
			newKeysAll.add(binding);
		}

		newKeysAll.addAll(registeredKeys);

		return newKeysAll.toArray(new KeyBinding[0]);
	}

	public static void registerKeyBinding(KeyBinding key) {
		registeredKeys.add(key);
	}
}
