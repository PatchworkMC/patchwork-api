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

package net.minecraftforge.client.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraftforge.client.extensions.IForgeKeybinding;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

/**
 * This is implementing Map to add vanilla compatibility.
 */
public class KeyBindingMap implements Map<InputUtil.KeyCode, KeyBinding> {
	private static final EnumMap<KeyModifier, Map<InputUtil.KeyCode, Collection<KeyBinding>>> map = new EnumMap<>(KeyModifier.class);

	static {
		for (KeyModifier modifier : KeyModifier.values()) {
			map.put(modifier, new HashMap<>());
		}
	}

	@Nullable
	public KeyBinding lookupActive(InputUtil.KeyCode keyCode) {
		KeyModifier activeModifier = KeyModifier.getActiveModifier();

		if (!activeModifier.matches(keyCode)) {
			KeyBinding binding = getBinding(keyCode, activeModifier);

			if (binding != null) {
				return binding;
			}
		}

		return getBinding(keyCode, KeyModifier.NONE);
	}

	@Nullable
	private KeyBinding getBinding(InputUtil.KeyCode keyCode, KeyModifier keyModifier) {
		Collection<KeyBinding> bindings = map.get(keyModifier).get(keyCode);

		if (bindings != null) {
			for (KeyBinding binding : bindings) {
				if (((IForgeKeybinding) binding).isActiveAndMatches(keyCode)) {
					return binding;
				}
			}
		}

		return null;
	}

	public List<KeyBinding> lookupAll(InputUtil.KeyCode keyCode) {
		List<KeyBinding> matchingBindings = new ArrayList<>();

		for (Map<InputUtil.KeyCode, Collection<KeyBinding>> bindingsMap : map.values()) {
			Collection<KeyBinding> bindings = bindingsMap.get(keyCode);

			if (bindings != null) {
				matchingBindings.addAll(bindings);
			}
		}

		return matchingBindings;
	}

	public void addKey(InputUtil.KeyCode keyCode, KeyBinding keyBinding) {
		KeyModifier keyModifier = ((IForgeKeybinding) keyBinding).getKeyModifier();
		Map<InputUtil.KeyCode, Collection<KeyBinding>> bindingsMap = map.get(keyModifier);
		Collection<KeyBinding> bindingsForKey = bindingsMap.get(keyCode);

		if (bindingsForKey == null) {
			bindingsForKey = new ArrayList<>();
			bindingsMap.put(keyCode, bindingsForKey);
		}

		bindingsForKey.add(keyBinding);
	}

	public void removeKey(KeyBinding keyBinding) {
		KeyModifier keyModifier = ((IForgeKeybinding) keyBinding).getKeyModifier();
		InputUtil.KeyCode keyCode = ((IForgeKeybinding) keyBinding).getKey();
		Map<InputUtil.KeyCode, Collection<KeyBinding>> bindingsMap = map.get(keyModifier);
		Collection<KeyBinding> bindingsForKey = bindingsMap.get(keyCode);

		if (bindingsForKey != null) {
			bindingsForKey.remove(keyBinding);

			if (bindingsForKey.isEmpty()) {
				bindingsMap.remove(keyCode);
			}
		}
	}

	public void clearMap() {
		for (Map<InputUtil.KeyCode, Collection<KeyBinding>> bindings : map.values()) {
			bindings.clear();
		}
	}

	@Override
	public int size() {
		int size = 0;

		for (Map<InputUtil.KeyCode, Collection<KeyBinding>> value : map.values()) {
			size += value.size();
		}

		return size;
	}

	@Override
	public boolean isEmpty() {
		for (Map<InputUtil.KeyCode, Collection<KeyBinding>> value : map.values()) {
			if (!isEmpty()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		for (Map<InputUtil.KeyCode, Collection<KeyBinding>> value : map.values()) {
			if (value.containsKey(key)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean containsValue(Object keybinding) {
		if (!(keybinding instanceof KeyBinding)) {
			return false;
		}

		KeyModifier keyModifier = ((IForgeKeybinding) keybinding).getKeyModifier();

		for (Entry<InputUtil.KeyCode, Collection<KeyBinding>> entry : map.get(keyModifier).entrySet()) {
			if (entry.getValue().contains(keybinding)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public KeyBinding get(Object key) {
		for (Map<InputUtil.KeyCode, Collection<KeyBinding>> valueMap : map.values()) {
			Collection<KeyBinding> bindings = valueMap.get(key);

			if (bindings != null && !bindings.isEmpty()) {
				return bindings.iterator().next();
			}
		}

		return null;
	}

	@Override
	public KeyBinding put(InputUtil.KeyCode key, KeyBinding value) {
		addKey(key, value);
		return value;
	}

	@Override
	public KeyBinding remove(Object key) {
		for (Map<InputUtil.KeyCode, Collection<KeyBinding>> value : map.values()) {
			Collection<KeyBinding> bindings = value.remove(key);

			if (bindings != null) {
				return bindings.iterator().next();
			}
		}

		return null;
	}

	@Override
	public void putAll(Map<? extends InputUtil.KeyCode, ? extends KeyBinding> m) {
		for (Entry<? extends InputUtil.KeyCode, ? extends KeyBinding> entry : m.entrySet()) {
			addKey(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		clearMap();
	}

	@Nonnull
	@Override
	public Set<InputUtil.KeyCode> keySet() {
		Set<InputUtil.KeyCode> keyCodes = Sets.newHashSet();

		for (Map<InputUtil.KeyCode, Collection<KeyBinding>> value : map.values()) {
			keyCodes.addAll(value.keySet());
		}

		return keyCodes;
	}

	@Nonnull
	@Override
	public Collection<KeyBinding> values() {
		Collection<KeyBinding> keyBindings = Lists.newArrayList();

		for (Map<InputUtil.KeyCode, Collection<KeyBinding>> value : map.values()) {
			for (Collection<KeyBinding> bindings : value.values()) {
				keyBindings.addAll(bindings);
			}
		}

		return keyBindings;
	}

	@Nonnull
	@Override
	public Set<Entry<InputUtil.KeyCode, KeyBinding>> entrySet() {
		Set<Entry<InputUtil.KeyCode, KeyBinding>> entries = Sets.newHashSet();

		for (Map<InputUtil.KeyCode, Collection<KeyBinding>> value : map.values()) {
			for (Entry<InputUtil.KeyCode, Collection<KeyBinding>> entry : value.entrySet()) {
				List<KeyBinding> bindingList = (List<KeyBinding>) entry.getValue();

				for (int i = 0; i < bindingList.size(); i++) {
					entries.add(new KeyCodeBindingsEntry(entry.getKey(), i, bindingList));
				}
			}
		}

		return entries;
	}

	private static class KeyCodeBindingsEntry implements Map.Entry<InputUtil.KeyCode, KeyBinding> {
		private final InputUtil.KeyCode code;
		private final int index;
		private final List<KeyBinding> bindingList;

		private KeyCodeBindingsEntry(InputUtil.KeyCode code, int index, List<KeyBinding> bindingList) {
			this.code = code;
			this.index = index;
			this.bindingList = bindingList;
		}

		@Override
		public InputUtil.KeyCode getKey() {
			return code;
		}

		@Override
		public KeyBinding getValue() {
			return bindingList.get(index);
		}

		@Override
		public KeyBinding setValue(KeyBinding value) {
			return bindingList.set(index, value);
		}
	}
}
