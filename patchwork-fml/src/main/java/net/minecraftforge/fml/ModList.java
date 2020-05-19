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

import java.util.Collections;
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
import net.fabricmc.loader.api.metadata.ModMetadata;

public class ModList {
	private static final Logger LOGGER = LogManager.getLogger();

	// Patchwork: initialize directly because there's no args
	private static ModList INSTANCE = new ModList();

	private Map<ModContainer, ModFileInfo> modFileInfoMap = new HashMap<>();
	private List<ModFileScanData> allScanDataCache;

	public static ModList get() {
		return INSTANCE;
	}

	public boolean isLoaded(String modId) {
		// Patchwork: use Fabric Loader lookup instead of an internal one
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	public ModFileInfo getModFileById(String modId) {
		ModContainer modContainer = FabricLoader.getInstance().getModContainer(modId).orElse(null);

		if (modContainer == null) {
			return null;
		}

		return getModFileByContainer(modContainer);
	}

	private ModFileInfo getModFileByContainer(ModContainer modContainer) {
		return modFileInfoMap.computeIfAbsent(modContainer, this::createModFileInfo);
	}

	private ModFileInfo createModFileInfo(ModContainer modContainer) {
		ModMetadata metadata = modContainer.getMetadata();

		// First try to find a patchwork:annotations entry for this file. If it exists, then this is the "primary" mod
		// for a given JAR file.
		CustomValue annotations = metadata.getCustomValue("patchwork:annotations");

		if (annotations != null) {
			String annotationJsonLocation = annotations.getAsString();

			return new ModFileInfo(modContainer, annotationJsonLocation);
		}

		// If there is no annotation data indicative of a primary mod file, try to then find the parent (primary) mod ID.
		// This indicates that this is a dummy JiJ mod created by Patchwork Patcher.
		CustomValue parent = metadata.getCustomValue("patchwork:parent");

		if (parent != null) {
			return getModFileById(parent.getAsString());
		}

		// This mod lacks annotation data or a parent mod ID.
		// Check to see if it was run through an old version of Patcher (if it lacks both the parent and annotations
		// attributes but has the source attribute)
		CustomValue source = modContainer.getMetadata().getCustomValue("patchwork:source");

		if (source != null) {
			CustomValue.CvObject object = source.getAsObject();
			String loader = object.get("loader").getAsString();

			if (loader.equals("forge")) {
				LOGGER.warn("A mod was patched with an old version of Patchwork Patcher, please re-patch it! "
						+ "No annotation data is available for " + metadata.getId() + " (loaded from " + modContainer.getRootPath() + ")");
			}
		}

		// Either a patchwork mod missing annotation data, or a normal Fabric mod.
		return new ModFileInfo();
	}

	public List<ModFileScanData> getAllScanData() {
		if (allScanDataCache == null) {
			// Even though ModFIleScanData lacks an implementation of Object#equals, the default implementation tests
			// for equality using object identity (a == b). In this case there is only one instance of ModFileScanData
			// for a given mod file (mod files can be shared by multiple mod containers), therefore comparison by object
			// identity alone (`==`) is sufficient.

			allScanDataCache = Collections.unmodifiableList(FabricLoader.getInstance().getAllMods()
					.stream()
					.map(modContainer -> modContainer.getMetadata().getId())
					.map(modid -> getModFileById(modid).getFile().getScanResult())
					.distinct()
					.collect(Collectors.toList()));
		}

		return allScanDataCache;
	}
}
