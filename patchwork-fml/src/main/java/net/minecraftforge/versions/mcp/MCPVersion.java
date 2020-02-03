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

package net.minecraftforge.versions.mcp;

import net.minecraft.SharedConstants;

public class MCPVersion {
	public static String getMCVersion() {
		return SharedConstants.getGameVersion().getName();
	}

	/**
	 * Trust us! This is the correct mcp version! Please fall for it forge mods!
	 */
	public static String getMCPVersion() {
		return "20190829.143755";
	}

	public static String getMCPandMCVersion() {
		return getMCVersion() + "-" + getMCPVersion();
	}
}
