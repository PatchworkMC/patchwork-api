/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2019, 2019
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

package com.patchworkmc.impl.fml;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cpw.mods.modlauncher.api.INameMappingService;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public class PatchworkMappingResolver {
	public static final String INTERMEDIARY = "intermediary";
	public static final String NAMED = "named";

	public static String remapName(INameMappingService.Domain domain, Class clazz, String name) {
		MappingResolver resolver = FabricLoader.getInstance().getMappingResolver();

		if (resolver.getCurrentRuntimeNamespace().equals(INTERMEDIARY)) {
			return name;
		}

		// Special-case for classes
		if (domain == INameMappingService.Domain.CLASS) {
			return resolver.mapClassName(NAMED, name);
		}

		String className = resolver.mapClassName(INTERMEDIARY, clazz.getName());

		// Verify format
		if (name.chars().filter(ch -> ch == '_').count() != 1) {
			throw new IllegalArgumentException("Expected valid intermediary name, got " + name);
		}

		// since intermediary is always unique, we can just iterate through all members
		// and find the one with the right name to get its descriptor
		switch (domain) {
		case METHOD:
			String methodDescriptor = null;

			for (Method method : clazz.getDeclaredMethods()) {
				// If the field name remapped to intermediary is the one we're looking for, store its descriptor and return
				if (resolver.mapMethodName(INTERMEDIARY, className, method.getName(),
						getMethodDescriptor(method, false)).equals(name)) {
					// We need to remap the descriptor to intermediary in case it contains named Minecraft classes
					methodDescriptor = getMethodDescriptor(method, true);
					break;
				}
			}

			return resolver.mapMethodName(NAMED, className, name, methodDescriptor);
		case FIELD:
			String fieldDescriptor = null;

			for (Field field : clazz.getDeclaredFields()) {
				// If the field name remapped to intermediary is the one we're looking for, store its descriptor and return
				if (resolver.mapFieldName(INTERMEDIARY, className, field.getName(),
						getDescriptorForClass(field.getType(), false)).equals(name)) {
					// We need to remap the descriptor to intermediary in case it contains named Minecraft classes
					fieldDescriptor = getDescriptorForClass(field.getType(), true);
					break;
				}
			}

			return resolver.mapFieldName(NAMED, className, name, fieldDescriptor);
		default:
			throw new IllegalArgumentException("Someone's been tampering with enums! Got unexpected type " + domain.name());
		}
	}

	// this is a stackoverflow response but with remapping
	private static String getDescriptorForClass(final Class<?> clazz, boolean remapToIntermediary) {
		if (clazz.isPrimitive()) {
			// This could technically be turned into a map lookup, but that seems like overengineering considering this will never change
			if (clazz == byte.class) {
				return "B";
			} else if (clazz == char.class) {
				return "C";
			} else if (clazz == double.class) {
				return "D";
			} else if (clazz == float.class) {
				return "F";
			} else if (clazz == int.class) {
				return "I";
			} else if (clazz == long.class) {
				return "J";
			} else if (clazz == short.class) {
				return "S";
			} else if (clazz == boolean.class) {
				return "Z";
			} else if (clazz == void.class) {
				return "V";
			} else {
				throw new RuntimeException("Unrecognized primitive " + clazz);
			}
		} else {
			String className = clazz.getName();

			if (remapToIntermediary) {
				className = FabricLoader.getInstance().getMappingResolver().mapClassName(INTERMEDIARY, className);
			}

			if (clazz.isArray()) {
				return className.replace('.', '/');
			} else {
				return ('L' + className + ';').replace('.', '/');
			}
		}
	}

	private static String getMethodDescriptor(Method method, boolean remapToIntermediary) {
		StringBuilder descriptor = new StringBuilder();
		descriptor.append("(");

		for (Class<?> clazz : method.getParameterTypes()) {
			descriptor.append(getDescriptorForClass(clazz, remapToIntermediary));
		}

		descriptor.append(")").append(getDescriptorForClass(method.getReturnType(), remapToIntermediary));
		return descriptor.toString();
	}
}
