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

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.registries.IForgeRegistry;

import net.minecraft.util.Identifier;

@SuppressWarnings("rawtypes")
public class RegistryClassMapping {
	private static final Map<Identifier, Class> ID_TO_CLASS = new HashMap<>();
	private static final Map<Class, Identifier> CLASS_TO_ID = new HashMap<>();

	/**
	 * Used by {@link net.minecraftforge.registries.ForgeRegistries}.
	 *
	 * @param registry the registry to add to the class mappings
	 */
	public static void register(IForgeRegistry registry) {
		register(registry.getRegistryName(), registry.getRegistrySuperType());
	}

	private static void register(Identifier identifier, Class clazz) {
		ID_TO_CLASS.put(identifier, clazz);
		CLASS_TO_ID.put(clazz, identifier);
	}

	public static Class<?> getClass(Identifier identifier) {
		return ID_TO_CLASS.get(identifier);
	}

	public static Identifier getIdentifier(Class<?> clazz) {
		Identifier existing = CLASS_TO_ID.get(clazz);

		if (existing != null) {
			return existing;
		}

		Class<?> superclass = clazz.getSuperclass();

		if (superclass == null || superclass == Object.class) {
			return null;
		} else {
			return getIdentifier(superclass);
		}
	}
}
