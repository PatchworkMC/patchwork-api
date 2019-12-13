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
 *     it contains a server, which can simulate the world and communicates via network.</li>
 * </ul>
 */
public enum Dist {

	/**
	 * The client distribution. This is the game client players can purchase and play.
	 * It contains the graphics and other rendering to present a viewport into the game world.
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
	 * @return If this marks a dedicated server.
	 */
	public boolean isDedicatedServer() {
		return !isClient();
	}

	/**
	 * @return if this marks a client.
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