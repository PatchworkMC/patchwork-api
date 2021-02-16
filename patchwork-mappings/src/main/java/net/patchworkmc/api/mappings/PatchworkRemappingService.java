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

package net.patchworkmc.api.mappings;

import java.util.HashMap;

import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.model.Mapping;
import org.cadixdev.lorenz.model.MethodMapping;

import net.patchworkmc.impl.mappings.PatchworkMappings;

/**
 * A class that Patchwork and mods that depend on it can use to map SRG names to
 * whatever the runtime mappings are.
 */
public class PatchworkRemappingService {
	/**
	 * Cache for method lookups so we can avoid doing a search every time.
	 */
	private static final HashMap<Class<?>, HashMap<String, String>> methodCache = new HashMap<>();

	public static String remapMethodName(Class<?> clazz, String srgName) {
		HashMap<String, String> cache = methodCache.computeIfAbsent(clazz, ignored -> new HashMap<>());

		if (cache.containsKey(srgName)) {
			return cache.get(srgName);
		}

		MappingSet runtime2srg = PatchworkMappings.getMappingGenerator().getRuntimeToSrgMappings();
		String runtimeName = runtime2srg.getClassMapping(clazz.getName())
				.map(classMapping -> {
					for (MethodMapping methodMapping : classMapping.getMethodMappings()) {
						if (methodMapping.getDeobfuscatedName().equals(srgName)) {
							return methodMapping.getObfuscatedName();
						}
					}

					return srgName;
				}).orElse(srgName);
		cache.put(srgName, runtimeName);
		return runtimeName;
	}

	public static String remapFieldName(Class<?> clazz, String srgName) {
		MappingSet runtime2srg = PatchworkMappings.getMappingGenerator().getRuntimeToSrgMappings();
		MappingSet srg2runtime = PatchworkMappings.getMappingGenerator().getSrgToRuntimeMappings();
		return runtime2srg.getClassMapping(clazz.getName())
				.map(classMapping -> srg2runtime.getClassMapping(classMapping.getObfuscatedName())
						.map(srgClassMapping -> srgClassMapping.getFieldMapping(srgName).map(Mapping::getDeobfuscatedName).orElse(srgName))
						.orElseThrow(() -> new IllegalStateException("PatchworkRemappingService tried to map class " + clazz.getName()
								+ " from runtime -> srg -> runtime, but the last step doesn't exist.")))
				.orElse(srgName);
	}
}
