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

import net.minecraft.util.Identifier;

public abstract class ForgeRegistryEntry<V extends IForgeRegistryEntry<V>> implements IForgeRegistryEntry<V> {
	private Identifier registryName;

	@Override
	public final V setRegistryName(Identifier name) {
		return setRegistryName(name.toString());
	}

	public final V setRegistryName(String name) {
		if (getRegistryName() != null) {
			throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + getRegistryName());
		}

		this.registryName = GameData.checkPrefix(name, true);
		return (V) this;
	}

	public final V setRegistryName(String modID, String name) {
		return setRegistryName(modID + ":" + name);
	}

	@Override
	public final Identifier getRegistryName() {
		return this.registryName;
	}

	@Override
	public Class<V> getRegistryType() {
		return (Class<V>) getClass();
	}
}
