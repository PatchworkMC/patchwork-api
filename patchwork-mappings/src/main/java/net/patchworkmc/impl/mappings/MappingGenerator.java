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

package net.patchworkmc.impl.mappings;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.srg.tsrg.TSrgReader;
import org.cadixdev.lorenz.merge.MappingSetMerger;
import org.cadixdev.lorenz.model.ClassMapping;
import org.jetbrains.annotations.NotNull;

import net.fabricmc.loader.launch.common.FabricLauncherBase;
import net.fabricmc.mapping.tree.ClassDef;
import net.fabricmc.mapping.tree.TinyTree;

public class MappingGenerator {
	private static final Logger LOGGER = LogManager.getLogger(MappingGenerator.class);
	private final MappingSet runtime2srg;
	private final MappingSet srg2runtime;
	private final String mcVersion;

	private static final Path workingDir = new File("./patchwork/data").toPath();
	private static final Path srgFile = workingDir.resolve("srg.tsrg");

	public MappingGenerator(String mcVersion) throws IOException {
		this.mcVersion = mcVersion;
		checkVersion();

		LOGGER.info("Loading obf -> runtime mappings from FabricLoader");
		MappingSet obf2runtime = loadObfToRuntime();

		LOGGER.info("Loading obf -> srg mappings from MCPConfig");

		if (!srgFile.toFile().exists()) {
			LOGGER.info("    downloading joined.tsrg from github.");
			downloadSrg();
		}

		MappingSet obf2srg = loadObfToSrg();

		LOGGER.info("Generating srg -> runtime mappings");
		MappingSetMerger merger = MappingSetMerger.create(obf2srg.reverse(), obf2runtime);
		srg2runtime = merger.merge();
		runtime2srg = srg2runtime.reverse();
	}

	private MappingSet loadObfToSrg() throws IOException {
		try (TSrgReader reader = new TSrgReader(new FileReader(srgFile.toFile()))) {
			return reader.read();
		}
	}

	public MappingSet getSrgToRuntimeMappings() {
		return srg2runtime;
	}

	public MappingSet getRuntimeToSrgMappings() {
		return runtime2srg;
	}

	@NotNull
	private MappingSet loadObfToRuntime() {
		TinyTree tinyTree = FabricLauncherBase.getLauncher().getMappingConfiguration().getMappings();
		MappingSet obf2int = MappingSet.create();
		String targetNamespace = FabricLauncherBase.getLauncher().getTargetNamespace();

		for (ClassDef classDef : tinyTree.getClasses()) {
			ClassMapping<?, ?> classMapping = obf2int.getOrCreateClassMapping(classDef.getName("official"));
			classMapping.setDeobfuscatedName(classDef.getName(targetNamespace));

			classDef.getFields().forEach(fieldDef -> classMapping.getOrCreateFieldMapping(fieldDef.getName("official"), fieldDef.getDescriptor("official")).setDeobfuscatedName(fieldDef.getName(targetNamespace)));
			classDef.getMethods().forEach(methodDef -> classMapping.getOrCreateMethodMapping(methodDef.getName("official"), methodDef.getDescriptor("official")).setDeobfuscatedName(methodDef.getName(targetNamespace)));
		}

		return obf2int;
	}

	private void checkVersion() throws IOException {
		boolean forceRedownload = false;

		if (!Files.exists(workingDir)) {
			Files.createDirectories(workingDir);
		}

		Path versionFile = workingDir.resolve("version");

		if (!Files.exists(versionFile)) {
			forceRedownload = true;
			FileUtils.write(versionFile.toFile(), mcVersion, StandardCharsets.UTF_8);
			LOGGER.info("Patchwork needs to download some files to generate its SRG to Intermediary mappings. "
					+ "Subsequent launches will be faster");
		} else {
			String s = FileUtils.readFileToString(versionFile.toFile(), StandardCharsets.UTF_8);

			if (!s.equals(mcVersion)) {
				LOGGER.info("Patchwork needs to download some files to update its SRG to Intermediary mappings "
						+ "to this version of Minecraft. Subsequent launches will be faster");
				forceRedownload = true;
				FileUtils.write(versionFile.toFile(), mcVersion, StandardCharsets.UTF_8);
			}
		}

		if (forceRedownload) {
			FileUtils.deleteQuietly(srgFile.toFile());
		}
	}

	private void downloadSrg() throws IOException {
		FileUtils.copyURLToFile(new URL("https://raw.githubusercontent.com/MinecraftForge/MCPConfig/master/versions/"
				+ "release/" + mcVersion + "/joined.tsrg"), srgFile.toFile());
	}
}
