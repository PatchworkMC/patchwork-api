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

package net.minecraftforge.fml.loading;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.loader.api.FabricLoader;

public enum FMLPaths {
	GAMEDIR(),
	MODSDIR("mods"),
	CONFIGDIR("config"),
	FMLCONFIG(false, CONFIGDIR, "fml.toml");

	private static final Logger LOGGER = LogManager.getLogger();
	private final Path relativePath;
	private final boolean isDirectory;
	private Path absolutePath;

	FMLPaths() {
		this("");
	}

	FMLPaths(String... path) {
		relativePath = computePath(path);
		this.isDirectory = true;
	}

	FMLPaths(boolean isDir, FMLPaths parent, String... path) {
		this.relativePath = parent.relativePath.resolve(computePath(path));
		this.isDirectory = isDir;
	}

	public static void loadAbsolutePaths(Path rootPath) {
		throw new UnsupportedOperationException("Implementation detail");
	}

	// TODO: Implementation Detail: public static void setup(IEnvironment env)

	public static Path getOrCreateGameRelativePath(Path path, String name) {
		return FileUtils.getOrCreateDirectory(FMLPaths.GAMEDIR.get().resolve(path), name);
	}

	private Path computePath(String... path) {
		return Paths.get(path[0], Arrays.copyOfRange(path, 1, path.length));
	}

	public Path relative() {
		return relativePath;
	}

	public Path get() {
		if (absolutePath == null) {
			Path rootPath = FabricLoader.getInstance().getGameDirectory().toPath();

			absolutePath = rootPath.resolve(relativePath).toAbsolutePath().normalize();

			if (isDirectory) {
				FileUtils.getOrCreateDirectory(absolutePath, name());
			}

			LOGGER.debug(CORE, "Path {} is {}", this, absolutePath);
		}

		return absolutePath;
	}
}
