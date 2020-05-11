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

package net.minecraftforge.fml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;

public class ModList {
	// Patchwork: initalize directly because there's no args
	private static ModList INSTANCE = new ModList();

	private Map<String, ModFileInfo> modFileInfoMap = new HashMap<>();

	private List<ModFileScanData> allScanDataCache;

	public static ModList get() {
		return INSTANCE;
	}

	public boolean isLoaded(String modId) {
		// Patchwork: use Fabric Loader lookup instead of an internal one
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	public ModFileInfo getModFileById(String modId) {
		return modFileInfoMap.computeIfAbsent(
				modId,
				k -> {
					String annotationHolderModid = getAnnotationHolderModid(modId);

					if (annotationHolderModid == null) {
						//Fabric mod
						return new ModFileInfo();
					}

					if (annotationHolderModid.equals(modId)) {
						//Patched mod and is not jij
						return new ModFileInfo(annotationHolderModid);
					}

					//jij patched mod
					return getModFileById(annotationHolderModid);
				}
		);
	}

	public List<ModFileScanData> getAllScanData() {
		if (allScanDataCache == null) {
			allScanDataCache = FabricLoader.getInstance().getAllMods()
					.stream()
					.map(modContainer -> modContainer.getMetadata().getId())
					.map(modid -> getModFileById(modid).getFile().getScanResult())
					.distinct()
					.collect(Collectors.toList());
		}

		return allScanDataCache;
	}

	//return null if it's a Fabric mod
	public static String getAnnotationHolderModid(String modid) {
		ModContainer modContainer = FabricLoader.getInstance().getModContainer(modid).orElseThrow(
				() -> new RuntimeException("No Mod Container for " + modid)
		);

		if (!ModFileInfo.isForgeMod(modContainer)) {
			return null;
		}

		//if it's a Forge mod, modmenu:parent can only be added by the patcher
		CustomValue parent = modContainer.getMetadata().getCustomValue("modmenu:parent");

		if (parent == null) {
			//it's not a jij mod
			return modid;
		}

		return parent.getAsString();
	}
}
