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

package net.minecraftforge.api.distmarker;

import net.fabricmc.api.EnvType;

/**
 * A distribution of the Minecraft game. There are two common distributions, and though
 * much code is common between them, there are some specific pieces that are only present
 * in one or the other.
 * <ul>
 *     <li>{@link #CLIENT} is the <em>client</em> distribution, it contains
 *     the game client, and has code to render a viewport into a game world.</li>
 *     <li>{@link #DEDICATED_SERVER} is the <em>dedicated server</em> distribution,
 *     it contains a server, which can simulate the world and communicates via the network.</li>
 * </ul>
 */
public enum Dist {
	/**
	 * The client distribution. This is the game client players can purchase and play.
	 * It contains graphics and other rendering to present a viewport into the game world.
	 */
	CLIENT,
	/**
	 * The dedicated server distribution. This is the server only distribution available for
	 * download. It simulates the world, and can be communicated with via a network.
	 * It contains no visual elements of the game whatsoever.
	 */
	DEDICATED_SERVER;

	public static Dist fromEnvType(EnvType type) {
		if (type == EnvType.CLIENT) {
			return CLIENT;
		} else {
			return DEDICATED_SERVER;
		}
	}

	/**
	 * @return {@code true} if this marks a dedicated server.
	 */
	public boolean isDedicatedServer() {
		return !isClient();
	}

	/**
	 * @return {@code true} if this marks a client.
	 */
	public boolean isClient() {
		return this == CLIENT;
	}

	public EnvType getEnvType() {
		if (this == CLIENT) {
			return EnvType.CLIENT;
		} else {
			return EnvType.SERVER;
		}
	}
}
