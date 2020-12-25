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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Master list of all mods - game-side version. This is classloaded in the game scope and
 * can dispatch game level events as a result.
 *
 * <p>
 * Patchwork note: Empty versions of these classes are created as needed from Loader's list of mods.
 */
public class ModList {
	private static Logger LOGGER = LogManager.getLogger();
	private static ModList INSTANCE;
	private final List<ModFileInfo> modFiles;
	//private final List<ModInfo> sortedList;
	private final Map<String, ModFileInfo> fileById;
	private List<ModContainer> mods;
	private Map<String, ModContainer> indexedMods;
	private List<ModFileScanData> modFileScanData;

	private ModList(final List<ModFile> modFiles) {
		this.modFiles = modFiles.stream().map(ModFile::getModFileInfo).map(ModFileInfo.class::cast).collect(Collectors.toList());
		//this.sortedList = sortedList.stream().
		//		map(ModInfo.class::cast).
		//		collect(Collectors.toList());
		this.fileById = this.modFiles.stream().map(ModFileInfo::getMods).flatMap(Collection::stream)
				.map(ModInfo.class::cast)
				.collect(Collectors.toMap(ModInfo::getModId, ModInfo::getOwningFile));
	}

	public static ModList get() {
		if (INSTANCE == null) {
			List<ModFile> modFiles = new ArrayList<>();

			for (net.fabricmc.loader.api.ModContainer fabricMod : FabricLoader.getInstance().getAllMods()) {
				modFiles.add(new ModFile(fabricMod));
			}

			INSTANCE = new ModList(modFiles);
		}

		return INSTANCE;
	}

	public List<ModFileInfo> getModFiles() {
		return modFiles;
	}

	public ModFileInfo getModFileById(String modid) {
		return this.fileById.get(modid);
	}

	public void setLoadedMods(final List<ModContainer> modContainers) {
		List<String> forgeIds = modContainers.stream().map(ModContainer::getModId).collect(Collectors.toList());

		for (net.fabricmc.loader.api.ModContainer cont : FabricLoader.getInstance().getAllMods()) {
			String id = cont.getMetadata().getId();

			if (!forgeIds.contains(id)) {
				modContainers.add(new FMLModContainer(cont.getMetadata().getId()));
			}
		}

		this.mods = modContainers;
		this.indexedMods = modContainers.stream().collect(Collectors.toMap(ModContainer::getModId, Function.identity()));
	}

	@SuppressWarnings("unchecked")
	public <T> Optional<T> getModObjectById(String modId) {
		return getModContainerById(modId).map(ModContainer::getMod).map(o -> (T) o);
	}

	public Optional<? extends ModContainer> getModContainerById(String modId) {
		return Optional.ofNullable(this.indexedMods.get(modId));
	}

	public Optional<? extends ModContainer> getModContainerByObject(Object obj) {
		return mods.stream().filter(mc -> mc.getMod() == obj).findFirst();
	}

	//public List<ModInfo> getMods() {
	//	return this.sortedList;
	//}

	public boolean isLoaded(String modTarget) {
		return this.indexedMods.containsKey(modTarget);
	}

	public int size() {
		return mods.size();
	}

	public List<ModFileScanData> getAllScanData() {
		if (modFileScanData == null) {
			modFileScanData = this.modFiles.stream()
					.map(ModFileInfo::patchwork$getInfo)
					.map(ModInfo::getOwningFile)
					.filter(Objects::nonNull)
					.map(ModFileInfo::getFile)
					.distinct()
					.map(ModFile::getScanResult)
					.collect(Collectors.toList());
		}

		return modFileScanData;
	}

	public void forEachModFile(Consumer<ModFile> fileConsumer) {
		modFiles.stream().map(ModFileInfo::getFile).forEach(fileConsumer);
	}

	public <T> Stream<T> applyForEachModFile(Function<ModFile, T> function) {
		return modFiles.stream().map(ModFileInfo::getFile).map(function);
	}

	public void forEachModContainer(BiConsumer<String, ModContainer> modContainerConsumer) {
		indexedMods.forEach(modContainerConsumer);
	}

	public <T> Stream<T> applyForEachModContainer(Function<ModContainer, T> function) {
		return indexedMods.values().stream().map(function);
	}
}
