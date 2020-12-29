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

import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.minecraft.util.Identifier;

/**
 * This serves as a replacement for ForgeRegistryEntry since we can't change the superclass of objects with Mixin.
 * @param <V>
 */
@SuppressWarnings("unchecked")
public interface ExtendedForgeRegistryEntry<V extends IForgeRegistryEntry<V>> extends IForgeRegistryEntry<V> {
	default V setRegistryName(String name) {
		if (getRegistryName() != null) {
			throw new IllegalStateException("Attempted to set registry name with existing registry name! New: "
					+ name + " Old: " + getRegistryName());
		}

		return this.setRegistryName(checkRegistryName(name));
	}

	default V setRegistryName(String namespace, String name) {
		return this.setRegistryName(new Identifier(namespace, name));
	}

	// package-private in forge, but we don't have the luxury of classes here
	default Identifier checkRegistryName(String name) {
		return GameData.checkPrefix(name, true);
	}

	@Override
	V setRegistryName(Identifier name);
}
