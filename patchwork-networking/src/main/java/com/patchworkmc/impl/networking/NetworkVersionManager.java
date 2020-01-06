/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2019, 2019
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

package com.patchworkmc.impl.networking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import net.minecraft.util.Identifier;

public final class NetworkVersionManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Marker NETREGISTRY = MarkerManager.getMarker("NETREGISTRY");
	public static final String ABSENT = "ABSENT \uD83E\uDD14";
	public static final String ACCEPTVANILLA = "ALLOWVANILLA \uD83D\uDC93\uD83D\uDC93\uD83D\uDC93";

	Iterable<NetworkChannel> channels;

	public NetworkVersionManager(Collection<NetworkChannel> channels) {
		this.channels = channels;
	}

	/**
	 * Construct the Map representation of the channel list, for use during login handshaking.
	 *
	 * @see FMLHandshakeMessages.S2CModList
	 * @see FMLHandshakeMessages.C2SModListReply
	 */
	public Map<Identifier, String> buildChannelVersions() {
		Map<Identifier, String> channelVersions = new HashMap<>();

		for (VersionedChannel channel: channels) {
			channelVersions.put(channel.getChannelName(), channel.getNetworkProtocolVersion());
		}

		return channelVersions;
	}

	/**
	 * Construct the Map representation of the channel list, for the client to check against during list ping.
	 *
	 * @see FMLHandshakeMessages.S2CModList
	 * @see FMLHandshakeMessages.C2SModListReply
	 */
	public Map<Identifier, Pair<String, Boolean>> buildChannelVersionsForListPing() {
		Map<Identifier, Pair<String, Boolean>> channelVersions = new HashMap<>();

		for (VersionedChannel channel: channels) {
			Identifier name = channel.getChannelName();

			if (name.getNamespace().equals("fml")) {
				continue;
			}

			Pair<String, Boolean> version = Pair.of(channel.getNetworkProtocolVersion(), channel.tryClientVersionOnServer(ABSENT));

			channelVersions.put(name, version);
		}

		return channelVersions;
	}

	public List<String> getServerNonVanillaNetworkMods() {
		return validateChannels(identifier -> ACCEPTVANILLA, Origin.VANILLA, VersionedChannel::tryClientVersionOnServer);
	}

	public List<String> getClientNonVanillaNetworkMods() {
		return validateChannels(identifier -> ACCEPTVANILLA, Origin.VANILLA, VersionedChannel::tryServerVersionOnClient);
	}

	/**
	 * Validate the channels from the server on the client. Tests the client predicates against the server
	 * supplied network protocol version.
	 *
	 * @param channels An @{@link Map} of name->version pairs for testing
	 * @return true if all channels accept themselves
	 */
	public boolean validateClientChannels(final Map<Identifier, String> channels) {
		return validateChannels(channels::get, Origin.SERVER, VersionedChannel::tryServerVersionOnClient).isEmpty();
	}

	/**
	 * Validate the channels from the client on the server. Tests the server predicates against the client
	 * supplied network protocol version.
	 * @param channels An @{@link Map} of name->version pairs for testing
	 * @return true if all channels accept themselves
	 */
	public boolean validateServerChannels(final Map<Identifier, String> channels) {
		return validateChannels(channels::get, Origin.CLIENT, VersionedChannel::tryClientVersionOnServer).isEmpty();
	}

	/**
	 * Tests if the map matches with the supplied predicate tester.
	 *
	 * @param incoming An @{@link Function} of name->version pairs for testing. It should return null for missing versions.
	 * @param origin A label for use in logging (where the version pairs came from)
	 * @param predicate A predicate to test whether a version satisfies the requirement
	 * @return a list of the channels that rejected the version check
	 */
	private List<String> validateChannels(final Function<Identifier, String> incoming, final Origin origin, BiPredicate<VersionedChannel, String> predicate) {
		List<String> rejected = new ArrayList<>();

		for (VersionedChannel channel: channels) {
			if (origin == Origin.PING && channel.getChannelName().getNamespace().equals("fml")) {
				// FML channels are not checked during the ping process.
				continue;
			}

			final String incomingVersion = incoming.apply(channel.getChannelName());
			final boolean accepted = predicate.test(channel, incomingVersion != null ? incomingVersion : ABSENT);

			if (!accepted) {
				rejected.add(channel.getChannelName().toString());
			}

			final String status = accepted ? "ACCEPTED" : "REJECTED";

			switch (origin) {
			case VANILLA:
				LOGGER.debug(NETREGISTRY, "Channel '{}' : Vanilla acceptance test: {}", channel.getChannelName(), status);
				break;
			case PING:
				LOGGER.debug(NETREGISTRY, "Channel '{}' : Version test of '{}' during listping : {}", channel.getChannelName(), incomingVersion, status);
				break;
			case SERVER:
			case CLIENT:
				LOGGER.debug(NETREGISTRY, "Channel '{}' : Version test of '{}' from {} : {}", channel.getChannelName(), incomingVersion, origin, status);
			}
		}

		if (!rejected.isEmpty()) {
			switch (origin) {
			case VANILLA:
				LOGGER.error(NETREGISTRY, "Channels {} rejected vanilla connections", rejected);
				break;
			case PING:
				LOGGER.error(NETREGISTRY, "Channels {} rejected their server side version number during listping", rejected);
				break;
			case SERVER:
			case CLIENT:
				LOGGER.error(NETREGISTRY, "Channels {} rejected their {} version number", rejected, origin);
			}

			return rejected;
		}

		if (origin != Origin.PING) {
			LOGGER.debug(NETREGISTRY, "Accepting channel list from {}", origin);
		}

		return Collections.emptyList();
	}

	public boolean checkListPingCompatibilityForClient(Map<Identifier, Pair<String, Boolean>> incoming) {
		List<String> rejected = validateChannels(identifier -> {
			Pair<String, Boolean> entry = incoming.get(identifier);

			return entry != null ? entry.getLeft() : null;
		}, Origin.PING, VersionedChannel::tryServerVersionOnClient);

		if (!rejected.isEmpty()) {
			return false;
		}

		Set<Identifier> handled = new HashSet<>();
		List<Identifier> missingButRequired = new ArrayList<>();

		for (NamedChannel channel: channels) {
			handled.add(channel.getChannelName());
		}

		for (Map.Entry<Identifier, Pair<String, Boolean>> entry : incoming.entrySet()) {
			Identifier channelName = entry.getKey();
			boolean required = entry.getValue().getRight();

			// We're looking for required and non FML channels that do not exist in our list of channels.
			if (!required || channelName.getNamespace().equals("fml") || handled.contains(channelName)) {
				continue;
			}

			missingButRequired.add(channelName);
		}

		if (!missingButRequired.isEmpty()) {
			LOGGER.error(NETREGISTRY, "The server is likely to require the channels {} to be present, yet we don't have them",
					missingButRequired);
			return false;
		}

		LOGGER.debug(NETREGISTRY, "Accepting channel list during listping");

		return true;
	}

	public enum Origin {
		CLIENT, SERVER, VANILLA,

		/**
		 * Server list ping response from server.
		 */
		PING
	}
}
