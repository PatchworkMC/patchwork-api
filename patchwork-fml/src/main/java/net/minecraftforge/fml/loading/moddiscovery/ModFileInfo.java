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

package net.minecraftforge.fml.loading.moddiscovery;

import java.util.Collections;
import java.util.List;

// TODO: stub
public class ModFileInfo {
	private final ModFile modFile;
	private final ModInfo modInfo;

	// Patchwork-specific constructor; forge one is internal so doesn't matter
	public ModFileInfo(ModFile file) {
		this.modFile = file;
		this.modInfo = new ModInfo(this);
	}

	public ModFile getFile() {
		return modFile;
	}

	public ModInfo patchwork$getInfo() {
		return modInfo;
	}

	// TODO: uses the modlauncher interface
	public List<ModInfo> getMods() {
		// patchwork: 1 modinfo per mod id, for now
		return Collections.singletonList(modInfo);
	}
}
