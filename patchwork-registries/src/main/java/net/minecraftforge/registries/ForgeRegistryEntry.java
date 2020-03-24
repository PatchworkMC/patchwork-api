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

public abstract class ForgeRegistryEntry<V> implements IForgeRegistryEntry<V> {
	private Identifier name;

	public final IForgeRegistryEntry setRegistryName(String name) {
		setRegistryName(new Identifier(name));
		return this;
	}

	public final IForgeRegistryEntry setRegistryName(String domain, String name) {
		setRegistryName(new Identifier(domain, name));
		return this;
	}

	@Override
	public final IForgeRegistryEntry setRegistryName(Identifier name) {
		this.name = name;

		return this;
	}

	@Override
	public final Identifier getRegistryName() {
		return this.name;
	}

	@Override
	public Class<V> getRegistryType() {
		return (Class<V>) getClass();
	}
}
