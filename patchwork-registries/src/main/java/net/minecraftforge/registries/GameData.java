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

package net.minecraftforge.registries;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.minecraftforge.fml.ModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;

public class GameData {
	private static final Logger LOGGER = LogManager.getLogger();

	public static final Map<Class<?>, RegistryKey<?>> patchwork$REGISTRY_MAP = new HashMap<>();
	static {
		init();
	}

	public static void init() {
		//
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static IForgeRegistry wrap(RegistryKey<?> registryKey, Class superClazz) {
		RegistryBuilder builder = new RegistryBuilder();
		builder.setName(registryKey.getValue());
		builder.setType(superClazz);
		builder.disableOverrides();	// Vanilla registry does not support override, modification is disabled by default
		builder.setVanillaRegistryKey(registryKey);
		patchwork$REGISTRY_MAP.put(superClazz, registryKey);
		return wrapVanilla(registryKey, builder);
	}

	/**
	 * Check a name for a domain prefix, and if not present infer it from the
	 * current active mod container.
	 *
	 * @param name          The name or resource location
	 * @param warnOverrides If true, logs a warning if domain differs from that of
	 *                      the currently currently active mod container
	 *
	 * @return The {@link Identifier} with given or inferred domain
	 */
	public static Identifier checkPrefix(String name, boolean warnOverrides) {
		int index = name.lastIndexOf(':');
		String oldPrefix = index == -1 ? "" : name.substring(0, index).toLowerCase(Locale.ROOT);
		name = index == -1 ? name : name.substring(index + 1);
		String prefix = ModLoadingContext.get().getActiveNamespace();

		if (warnOverrides && !oldPrefix.equals(prefix) && oldPrefix.length() > 0) {
			LogManager.getLogger().info("Potentially Dangerous alternative prefix `{}` for name `{}`, expected `{}`. This could be a intended override, but in most cases indicates a broken mod.", oldPrefix, name, prefix);
			throw new IllegalArgumentException("Patchwork does not support registry replacement!");
			//prefix = oldPrefix;
		}

		return new Identifier(prefix, name);
	}

	@SuppressWarnings("unchecked")
	private static <V extends IForgeRegistryEntry<V>> IForgeRegistry<V> wrapVanilla(RegistryKey<?> forgeId, RegistryBuilder<V> builder) {
		if (builder == null) {
			LOGGER.warn("Detected an unknown Vanilla registry with no Patchwork equivalent: %s", forgeId);
			return null;
		}

		return builder.create();
	}
}
