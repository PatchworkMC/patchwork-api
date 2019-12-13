/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.fml;

import java.util.EnumMap;

import net.minecraftforge.fml.config.ModConfig;

// TODO: Stub
public class ModContainer {
	protected final String modId;
	protected final String namespace;
	protected final EnumMap<ModConfig.Type, ModConfig> configs;

	public ModContainer(String modId) {
		this.modId = modId;
		// TODO: Currently not reading namespace from configuration..
		this.namespace = modId;
		this.configs = new EnumMap<>(ModConfig.Type.class);
	}

	public final String getModId() {
		return modId;
	}

	public final String getNamespace() {
		return namespace;
	}

	public void addConfig(final ModConfig modConfig) {
		configs.put(modConfig.getType(), modConfig);
	}
}
