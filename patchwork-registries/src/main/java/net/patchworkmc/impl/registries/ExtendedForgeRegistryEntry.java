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

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.minecraft.util.Identifier;

@SuppressWarnings("unchecked")
public interface ExtendedForgeRegistryEntry<V> extends IForgeRegistryEntry<V> {
	default IForgeRegistryEntry<V> setRegistryName(String full) {
		String activeNamespace = ModLoadingContext.get().getActiveNamespace();

		if (activeNamespace == null || activeNamespace.equals("minecraft")) {
			System.err.println("Currently active namespace is minecraft while registering item: " + full);
		}

		Identifier identifier;

		if (full.contains(":")) {
			identifier = new Identifier(full);
		} else {
			identifier = new Identifier(activeNamespace, full);
		}

		if (!identifier.getNamespace().equals(activeNamespace)) {
			System.err.printf("Potentially Dangerous alternative prefix `%s` for name `%s`, expected `%s`. This could be a intended override, but in most cases indicates a broken mod.\n", identifier.getNamespace(), identifier.getPath(), activeNamespace);
		}

		return (IForgeRegistryEntry<V>) this.setRegistryName(identifier);
	}

	default IForgeRegistryEntry<V> setRegistryName(String namespace, String name) {
		return (IForgeRegistryEntry<V>) this.setRegistryName(new Identifier(namespace, name));
	}
}
