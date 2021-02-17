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

package net.minecraftforge.fml.client;

import java.io.File;

import org.jetbrains.annotations.Nullable;

import org.apache.commons.lang3.NotImplementedException;

import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.ServerMetadata;
import net.minecraft.util.Identifier;
import net.minecraft.world.level.storage.LevelSummary;

import net.patchworkmc.impl.networking.ClientNetworkingEvents;
import net.patchworkmc.annotations.Stubbed;

/**
 * A stubbed out copy of Forge's ClientHooks, intended for use by Forge mods only.
 * For methods that you are implementing, don't keep implementation details here.
 * Elements should be thin wrappers around methods in other modules.
 * Do not depend on this class in other modules.
 */
public class ClientHooks {
	//private static final Logger LOGGER = LogManager.getLogger();
	//private static final Marker CLIENTHOOKS = MarkerManager.getMarker("CLIENTHOOKS");
	// From FontRenderer.renderCharAtPos
	//private static final String ALLOWED_CHARS = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000";
	//private static final CharMatcher DISALLOWED_CHAR_MATCHER = CharMatcher.anyOf(ALLOWED_CHARS).negate();

	//private static final Identifier iconSheet = new Identifier(ForgeVersion.MOD_ID, "textures/gui/icons.png");
	//private static SetMultimap<String, Identifier> missingTextures = HashMultimap.create();
	//private static Set<String> badTextureDomains = Sets.newHashSet();
	//private static Table<String, String, Set<Identifier>> brokenTextures = HashBasedTable.create();

	@Stubbed
	@Nullable
	public static void processForgeListPingData(ServerMetadata packet, MultiplayerServerListWidget.ServerEntry target) {
		throw new NotImplementedException("ClientHooks stub");
	}

	@Stubbed
	public static void drawForgePingInfo(MultiplayerScreen gui, MultiplayerServerListWidget.ServerEntry target, int x, int y, int width, int relativeMouseX, int relativeMouseY) {
		throw new NotImplementedException("ClientHooks stub");
	}

	public static String fixDescription(String description) {
		return description.endsWith(":NOFML§r") ? description.substring(0, description.length() - 8) + "§r" : description;
	}

	@Stubbed
	static File getSavesDir() {
		throw new NotImplementedException("ClientHooks stub");
	}

	@Stubbed
	public static void tryLoadExistingWorld(SelectWorldScreen selectWorldGUI, LevelSummary comparator) {
		throw new NotImplementedException("ClientHooks stub");
	}

	@Stubbed
	private static ClientConnection getClientToServerNetworkManager() {
		throw new NotImplementedException("ClientHooks stub");
	}

	@Stubbed
	public static void handleClientWorldClosing(ClientWorld world) {
		throw new NotImplementedException("ClientHooks stub");
	}

	@Stubbed
	public static String stripSpecialChars(String message) {
		throw new NotImplementedException("ClientHooks stub");
	}

	@Stubbed
	public static void trackMissingTexture(Identifier resourceLocation) {
		throw new NotImplementedException("ClientHooks stub");
	}

	@Stubbed
	public static void trackBrokenTexture(Identifier resourceLocation, String error) {
		throw new NotImplementedException("ClientHooks stub");
	}

	@Stubbed
	public static void logMissingTextureErrors() {
		throw new NotImplementedException("ClientHooks stub");
	}

	public static void firePlayerLogin(final ClientPlayerInteractionManager interactionManager, final ClientPlayerEntity player, final ClientConnection clientConnection) {
		ClientNetworkingEvents.firePlayerLogin(interactionManager, player, clientConnection);
	}

	public static void firePlayerLogout(final ClientPlayerInteractionManager interactionManager, final ClientPlayerEntity player) {
		ClientNetworkingEvents.firePlayerLogout(interactionManager, player);
	}

	public static void firePlayerRespawn(final ClientPlayerInteractionManager interactionManager, final ClientPlayerEntity oldPlayer, final ClientPlayerEntity newPlayer, final ClientConnection clientConnection) {
		ClientNetworkingEvents.firePlayerRespawn(interactionManager, oldPlayer, newPlayer, clientConnection);
	}
}
