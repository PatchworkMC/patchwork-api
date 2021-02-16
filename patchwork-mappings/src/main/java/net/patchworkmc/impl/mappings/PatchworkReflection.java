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

package net.patchworkmc.impl.mappings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.model.Mapping;

import net.patchworkmc.api.mappings.PatchworkRemappingService;

/**
 * Hooks for patcher to replace calls to the Java Reflection API with.
 */
public class PatchworkReflection {
	public static Class<?> forName(String name) throws ClassNotFoundException {
		MappingSet srgToRuntimeMappings = PatchworkMappings.getMappingGenerator().getSrgToRuntimeMappings();
		String remapped = srgToRuntimeMappings.getClassMapping(name).map(Mapping::getDeobfuscatedName).orElse(name);
		return Class.forName(remapped);
	}

	public static Field getDeclaredField(Class<?> clazz, String name) throws NoSuchFieldException {
		String remapped = PatchworkRemappingService.remapFieldName(clazz, name);
		return clazz.getDeclaredField(remapped);
	}

	public static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... paramTypes) throws NoSuchMethodException {
		String remapped = PatchworkRemappingService.remapMethodName(clazz, name);
		return clazz.getDeclaredMethod(remapped, paramTypes);
	}
}
