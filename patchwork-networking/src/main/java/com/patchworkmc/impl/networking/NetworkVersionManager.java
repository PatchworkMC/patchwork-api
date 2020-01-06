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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import net.minecraftforge.fml.network.NetworkInstance;
import net.minecraftforge.fml.network.NetworkRegistry;
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

	Iterable<NetworkInstance> channels;

	public NetworkVersionManager(Collection<NetworkInstance> channels) {
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

			Pair<String, Boolean> version = Pair.of(channel.getNetworkProtocolVersion(), channel.tryClientVersionOnServer(NetworkRegistry.ABSENT));

			channelVersions.put(name, version);
		}

		return channelVersions;
	}

	public List<String> getServerNonVanillaNetworkMods() {
		return validateChannels(identifier -> NetworkRegistry.ACCEPTVANILLA, Origin.VANILLA, VersionedChannel::tryClientVersionOnServer);
	}

	public List<String> getClientNonVanillaNetworkMods() {
		return validateChannels(identifier -> NetworkRegistry.ACCEPTVANILLA, Origin.VANILLA, VersionedChannel::tryServerVersionOnClient);
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
			final String incomingVersion = incoming.apply(channel.getChannelName());
			final boolean accepted = predicate.test(channel, incomingVersion != null ? incomingVersion : NetworkRegistry.ABSENT);

			if (origin == Origin.VANILLA) {
				LOGGER.debug(NETREGISTRY, "Channel '{}' : Vanilla acceptance test: {}", channel.getChannelName(), accepted ? "ACCEPTED" : "REJECTED");
			} else {
				LOGGER.debug(NETREGISTRY, "Channel '{}' : Version test of '{}' from {} : {}", channel.getChannelName(), incomingVersion, origin, accepted ? "ACCEPTED" : "REJECTED");
			}

			if (!accepted) {
				rejected.add(channel.getChannelName().toString());
			}
		}

		if (!rejected.isEmpty()) {
			if (origin == Origin.VANILLA) {
				LOGGER.error(NETREGISTRY, "Channels {} rejected vanilla connections", rejected);
			} else {
				LOGGER.error(NETREGISTRY, "Channels {} rejected their {} version number", rejected, origin);
			}

			return rejected;
		}

		LOGGER.debug(NETREGISTRY, "Accepting channel list from {}", origin);

		return Collections.emptyList();
	}

	public boolean checkListPingCompatibilityForClient(Map<Identifier, Pair<String, Boolean>> incoming) {
		Set<Identifier> handled = new HashSet<>();
		final List<Pair<Identifier, Boolean>> results = StreamSupport.stream(channels.spliterator(), false)
				.filter(p -> !p.getChannelName().getNamespace().equals("fml"))
				.map(ni -> {
					final Pair<String, Boolean> incomingVersion = incoming.getOrDefault(ni.getChannelName(), Pair.of(ABSENT, true));
					final boolean test = ni.tryServerVersionOnClient(incomingVersion.getLeft());
					handled.add(ni.getChannelName());
					LOGGER.debug(NETREGISTRY, "Channel '{}' : Version test of '{}' during listping : {}", ni.getChannelName(), incomingVersion, test ? "ACCEPTED" : "REJECTED");
					return Pair.of(ni.getChannelName(), test);
				}).filter(p -> !p.getRight()).collect(Collectors.toList());
		final List<Identifier> missingButRequired = incoming.entrySet().stream()
				.filter(p -> !p.getKey().getNamespace().equals("fml"))
				.filter(p -> !p.getValue().getRight())
				.filter(p -> !handled.contains(p.getKey()))
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());

		if (!results.isEmpty()) {
			LOGGER.error(NETREGISTRY, "Channels [{}] rejected their server side version number during listping",
					results.stream().map(Pair::getLeft).map(Object::toString).collect(Collectors.joining(",")));
			return false;
		}

		if (!missingButRequired.isEmpty()) {
			LOGGER.error(NETREGISTRY, "The server is likely to require channel [{}] to be present, yet we don't have it",
					missingButRequired);
			return false;
		}

		LOGGER.debug(NETREGISTRY, "Accepting channel list during listping");
		return true;
	}

	public enum Origin {
		CLIENT, SERVER, VANILLA
	}
}
