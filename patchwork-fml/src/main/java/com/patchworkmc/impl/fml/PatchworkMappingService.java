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

package com.patchworkmc.impl.fml;

import static net.minecraftforge.fml.common.ObfuscationReflectionHelper.INTERMEDIARY;
import static net.minecraftforge.fml.common.ObfuscationReflectionHelper.MAPPINGS;
import static net.minecraftforge.fml.common.ObfuscationReflectionHelper.NAMED;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cpw.mods.modlauncher.api.INameMappingService;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.mapping.tree.ClassDef;
import net.fabricmc.mapping.tree.Mapped;

public class PatchworkMappingService {
	private PatchworkMappingService() {
		// NO-OP
	}

	/**
	 * Remaps a name from intermediary to whatever is currently being used at runtime.
	 *
	 * @param domain    The {@link INameMappingService.Domain} to look up.
	 * @param name      The name to try and remap.
	 * @return The remapped name, or the original name if it couldn't be remapped.
	 */
	@Nonnull
	public static String remapName(INameMappingService.Domain domain, String name) {
		if (FabricLoader.getInstance().getMappingResolver().getCurrentRuntimeNamespace().equals(INTERMEDIARY)) {
			return name;
		}

		if (domain == INameMappingService.Domain.CLASS) {
			return MAPPINGS.getDefaultNamespaceClassMap().get(name).getName(NAMED);
		}

		String remappedName;

		for (ClassDef classDef : MAPPINGS.getClasses()) {
			remappedName = PatchworkMappingService.remapNameInternal(domain, classDef, name);

			if (remappedName != null) {
				return remappedName;
			}
		}

		return name;
	}

	/**
	 * Like {@link PatchworkMappingService#remapName(INameMappingService.Domain, String)}, but only iterates through members of the target class.
	 * @param domain    The {@link INameMappingService.Domain} to look up.
	 * @param clazz     The class that contains the {@code name} to look up.
	 * @param name      The name to remap.
	 * @return The remapped name, or the original name if it couldn't be remapped.
	 */
	@Nonnull
	public static String remapNameFast(INameMappingService.Domain domain, Class<?> clazz, String name) {
		ClassDef classDef = MAPPINGS.getDefaultNamespaceClassMap().get(clazz.getName());
		String remappedName = remapNameInternal(domain, classDef, name);

		return remappedName != null ? remappedName : name;
	}

	/**
	 * Like {@link PatchworkMappingService#remapNameFast(INameMappingService.Domain, Class, String)}, but takes a {@link ClassDef} instead of a {@link Class}.
	 * @param domain    The {@link INameMappingService.Domain} to look up.
	 * @param classDef  The classDef that contains the {@code name} to look up.
	 * @param name      The name to remap.
	 * @return The remapped name, or null if it couldn't be remapped.
	 */
	@Nullable
	public static String remapNameInternal(INameMappingService.Domain domain, ClassDef classDef, String name) {
		if (FabricLoader.getInstance().getMappingResolver().getCurrentRuntimeNamespace().equals(INTERMEDIARY)) {
			return name;
		}

		if (domain == INameMappingService.Domain.CLASS) {
			return classDef.getName(name);
		}

		boolean domainIsMethod = domain == INameMappingService.Domain.METHOD;

		for (Mapped mapped : domainIsMethod ? classDef.getMethods() : classDef.getFields()) {
			if (mapped.getName(INTERMEDIARY).equals(name)) {
				return mapped.getName(NAMED);
			}
		}

		return null;
	}
}
