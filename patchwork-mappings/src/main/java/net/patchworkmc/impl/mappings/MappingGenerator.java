package net.patchworkmc.impl.mappings;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.srg.tsrg.TSrgReader;
import org.cadixdev.lorenz.merge.MappingSetMerger;
import org.cadixdev.lorenz.merge.MergeConfig;
import org.cadixdev.lorenz.model.ClassMapping;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.jetbrains.annotations.NotNull;

import net.fabricmc.loader.launch.common.FabricLauncherBase;
import net.fabricmc.mapping.tree.ClassDef;
import net.fabricmc.mapping.tree.TinyTree;

public class MappingGenerator {
	private static final String FORGE_MAVEN = "https://files.minecraftforge.net/maven";
	private static final Logger LOGGER = LogManager.getLogger(MappingGenerator.class);

	private static final Path workingDir = new File("./patchwork/data").toPath();

	private static MappingSet bridged = null;

	public static MappingSet getOrGenerateSrgToIntMappings(String mcVersion) throws IOException {
		if (bridged == null) {
			checkVersion(mcVersion);

			LOGGER.debug("Loading obf -> intermediary mappings");
			MappingSet obf2int = loadObfToIntermediary();

			Path srg = workingDir.resolve("srg.tsrg");

			if (Files.notExists(srg)) {
				downloadSrg(mcVersion, srg);
			}

			LOGGER.debug("Loading obf -> srg mappings");

			MappingSet obf2srg = loadObfToSrg();

			LOGGER.debug("Generating intermediary -> srg mappings");

			MappingSet int2obf = obf2int.reverse();

			MappingSetMerger merger = MappingSetMerger.create(int2obf, obf2srg, MergeConfig.builder().withMergeHandler(new PatchworkMergerHandler()).build());

			bridged = merger.merge().reverse();
		}

		return bridged;
	}

	private static MappingSet loadObfToSrg() throws IOException {
		Path srg = workingDir.resolve("srg.tsrg");

		try (TSrgReader reader = new TSrgReader(new FileReader(srg.toFile()))) {
			return reader.read();
		}
	}

	private static void checkVersion(String mcVersion) throws IOException {
		boolean forceRedownload = false;
		if (!Files.exists(workingDir)) {
			Files.createDirectories(workingDir);
		}
		Path versionFile = workingDir.resolve("version");

		if (!Files.exists(versionFile)) {
			forceRedownload = true;
			com.google.common.io.Files.write(mcVersion, versionFile.toFile(), StandardCharsets.UTF_8);
			LOGGER.info("Patchwork needs to download some files to generate its SRG to Intermediary mappings. " +
							"Subsequent launches will be faster");
		} else {
			String s = com.google.common.io.Files.toString(versionFile.toFile(), StandardCharsets.UTF_8);
			if (!s.equals(mcVersion)) {
				LOGGER.info("Patchwork needs to download some files to update its SRG to Intermediary mappings " +
								"to this version of Minecraft. Subsequent launches will be faster");
				forceRedownload = true;
				com.google.common.io.Files.write(mcVersion, versionFile.toFile(), StandardCharsets.UTF_8);
			}
		}

		if (forceRedownload) {
			Files.deleteIfExists(workingDir.resolve("srg.tsrg"));
			Files.deleteIfExists(workingDir.resolve("mcp-config.zip"));
		}
	}

	private static void downloadSrg(String mcVersion, Path srg) throws IOException {
		LOGGER.info("Downloading srg mappings...");
		Path mcpConfig = workingDir.resolve("mcp-config.zip");
		String mcpVersion = getMcpVersion(mcVersion);
		FileUtils.copyURLToFile(new URL(FORGE_MAVEN + "/de/oceanlabs/mcp/mcp_config/" + mcpVersion
						+ "/mcp_config-" + mcpVersion + ".zip"), mcpConfig.toFile());

		LOGGER.info("Unpacking srg mappings...");

		try {
			URI inputJar = new URI("jar:" + mcpConfig.toUri());

			try (FileSystem fs = FileSystems.newFileSystem(inputJar, Collections.emptyMap())) {
				Files.copy(fs.getPath("/config/joined.tsrg"), srg);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@NotNull
	private static MappingSet loadObfToIntermediary() {
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

	private static String getMcpVersion(String mcVersion) throws IOException {
		Document document = null;
		try {
			document = new SAXReader().read(new URL(FORGE_MAVEN + "/de/oceanlabs/mcp/mcp_config/maven-metadata.xml"));
		} catch (DocumentException e) {
			throw new IOException(e);
		}
		List<Node> nodes = document.selectNodes("/metadata/versioning/versions/version");
		Collections.reverse(nodes);
		for (Node node : nodes) {
			if (node.getText().startsWith(mcVersion)) {
				return node.getText();
			}
		}
		throw new IllegalArgumentException("Could not find MCPConfig for minecraft version " + mcVersion);
	}
}
