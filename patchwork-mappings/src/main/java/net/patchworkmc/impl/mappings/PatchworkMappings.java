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

import java.io.IOException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.SharedConstants;

import net.fabricmc.api.ModInitializer;

public class PatchworkMappings implements ModInitializer {
	private static final Logger LOGGER = LogManager.getLogger();
	private static MappingGenerator mappingGenerator;
	private static IOException exception;

	@Override
	public void onInitialize() {
		try {
			mappingGenerator = new MappingGenerator(SharedConstants.getGameVersion().getReleaseTarget());
		} catch (IOException e) {
			LOGGER.log(Level.ERROR, "An unexpected error occurred while generating mappings for Patchwork", e);
			exception = e;
		}
	}

	public static MappingGenerator getMappingGenerator() {
		if (mappingGenerator == null) {
			throw new RuntimeException("A mod tried to use reflection but the mappings are not loaded. Is the game offline?", exception);
		} else {
			return mappingGenerator;
		}
	}
}
