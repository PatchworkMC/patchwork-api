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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;

public class ModList {
	private static final Logger LOGGER = LogManager.getLogger();

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
						//non-patched mod or invalid mod
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

	//return null if it does not have annotation data
	private static String getAnnotationHolderModid(String modid) {
		ModContainer modContainer = FabricLoader.getInstance().getModContainer(modid).orElse(null);

		if (modContainer == null) {
			LOGGER.error("Trying to access annotation data of a missing mod " + modid);
			LOGGER.catching(new Throwable());
			return null;
		}

		if (!isPatchedMod(modContainer)) {
			return null;
		}

		CustomValue parent = modContainer.getMetadata().getCustomValue("patchwork:parent");

		if (parent == null) {
			//it's not a jij mod
			return modid;
		}

		return parent.getAsString();
	}

	public static boolean isPatchedMod(ModContainer modContainer) {
		CustomValue source = modContainer.getMetadata().getCustomValue("patchwork:source");

		if (source == null) {
			return false;
		}

		CustomValue.CvObject object = source.getAsObject();
		String loader = object.get("loader").getAsString();
		return loader.equals("forge");
	}
}
