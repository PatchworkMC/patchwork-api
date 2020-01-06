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

package net.minecraftforge.fml.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraftforge.fml.network.event.EventNetworkChannel;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import com.patchworkmc.impl.networking.ListenableChannel;
import com.patchworkmc.impl.networking.NetworkChannel;
import com.patchworkmc.impl.networking.NetworkVersionManager;

/**
 * The network registry. Tracks channels on behalf of mods.
 */
public class NetworkRegistry {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Marker NETREGISTRY = MarkerManager.getMarker("NETREGISTRY");
	/**
	 * Special value for clientAcceptedVersions and serverAcceptedVersions predicates indicating the other side lacks
	 * this channel.
	 */
	public static final String ABSENT = NetworkVersionManager.ABSENT;
	public static final String ACCEPTVANILLA = NetworkVersionManager.ACCEPTVANILLA;

	private static Map<Identifier, NetworkChannel> channels = Collections.synchronizedMap(new HashMap<>());
	private static NetworkVersionManager versionManager = new NetworkVersionManager(channels.values());
	private static boolean lock = false;

	public static List<String> getServerNonVanillaNetworkMods() {
		return versionManager.getServerNonVanillaNetworkMods();
	}

	public static List<String> getClientNonVanillaNetworkMods() {
		return versionManager.getClientNonVanillaNetworkMods();
	}

	public static boolean acceptsVanillaClientConnections() {
		return channels.isEmpty() || getServerNonVanillaNetworkMods().isEmpty();
	}

	public static boolean canConnectToVanillaServer() {
		return channels.isEmpty() || getClientNonVanillaNetworkMods().isEmpty();
	}

	/**
	 * Create a new {@link SimpleChannel}.
	 *
	 * @param name The registry name for this channel. Must be unique
	 * @param networkProtocolVersion The network protocol version string that will be offered to the remote side {@link ChannelBuilder#networkProtocolVersion(Supplier)}
	 * @param clientAcceptedVersions Called on the client with the networkProtocolVersion string from the server {@link ChannelBuilder#clientAcceptedVersions(Predicate)}
	 * @param serverAcceptedVersions Called on the server with the networkProtocolVersion string from the client {@link ChannelBuilder#serverAcceptedVersions(Predicate)}
	 * @return A new {@link SimpleChannel}
	 *
	 * @see ChannelBuilder#newSimpleChannel(Identifier, Supplier, Predicate, Predicate)
	 */
	public static SimpleChannel newSimpleChannel(final Identifier name, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
		return new SimpleChannel(name, createChannel(name, networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions));
	}

	/**
	 * Create a new {@link EventNetworkChannel}.
	 *
	 * @param name The registry name for this channel. Must be unique
	 * @param networkProtocolVersion The network protocol version string that will be offered to the remote side {@link ChannelBuilder#networkProtocolVersion(Supplier)}
	 * @param clientAcceptedVersions Called on the client with the networkProtocolVersion string from the server {@link ChannelBuilder#clientAcceptedVersions(Predicate)}
	 * @param serverAcceptedVersions Called on the server with the networkProtocolVersion string from the client {@link ChannelBuilder#serverAcceptedVersions(Predicate)}
	 * @return A new {@link EventNetworkChannel}
	 *
	 * @see ChannelBuilder#newEventChannel(Identifier, Supplier, Predicate, Predicate)
	 */
	public static EventNetworkChannel newEventChannel(final Identifier name, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
		return new EventNetworkChannel(createChannel(name, networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions));
	}

	/**
	 * Creates the internal {@link NetworkChannel} that tracks the channel data.
	 * @param name registry name
	 * @param networkProtocolVersion The protocol version string
	 * @param clientAcceptedVersions The client accepted predicate
	 * @param serverAcceptedVersions The server accepted predicate
	 * @return The {@link ListenableChannel} for hooking up listeners to the created channel.
	 * @throws IllegalArgumentException if the name already exists
	 */
	private static ListenableChannel createChannel(Identifier name, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
		if (lock) {
			LOGGER.error(NETREGISTRY, "Attempted to register channel {} to a locked NetworkRegistry, the registry phase is over", name);
			throw new IllegalArgumentException("Registration of network channels is locked");
		}

		if (channels.containsKey(name)) {
			LOGGER.error(NETREGISTRY, "Attempted to register a channel with the name {}, but there is already a channel registered with that name.", name);
			throw new IllegalArgumentException("Channel {" + name + "} already registered");
		}

		final NetworkChannel channel = new NetworkChannel(name, networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions);
		channels.put(name, channel);

		return channel;
	}

	/**
	 * Find the {@link NetworkChannel}, if possible.
	 *
	 * @param identifier The {@link Identifier} of the network channel to lookup
	 * @return The {@link Nullable} {@link NetworkChannel}
	 */
	@Nullable
	static NetworkChannel findTarget(Identifier identifier) {
		return channels.get(identifier);
	}

	/**
	 * Retrieve the {@link LoginPayload} list for dispatch during {@link FMLHandshakeHandler#tickLogin(net.minecraft.network.ClientConnection)} handling.
	 * Dispatches {@link net.minecraftforge.fml.network.NetworkEvent.GatherLoginPayloadsEvent} to each {@link NetworkChannel}.
	 *
	 * @param direction the network direction for the request - only gathers for LOGIN_TO_CLIENT
	 * 	 * @return The {@link LoginPayload} list
	 */
	static List<LoginPayload> gatherLoginPayloads(final NetworkDirection direction, boolean isLocal) {
		if (direction != NetworkDirection.LOGIN_TO_CLIENT) {
			return Collections.emptyList();
		}

		List<LoginPayload> gatheredPayloads = new ArrayList<>();

		for (NetworkChannel channel: channels.values()) {
			channel.onGatherLoginPayloads(gatheredPayloads, isLocal);
		}

		return gatheredPayloads;
	}

	public static void lock() {
		lock = true;
	}

	public boolean isLocked() {
		return lock;
	}

	/**
	 * Tracks individual outbound messages for dispatch to clients during login handling. Gathered by dispatching
	 * {@link net.minecraftforge.fml.network.NetworkEvent.GatherLoginPayloadsEvent} during early connection handling.
	 */
	public static class LoginPayload {
		/**
		 * The data for sending.
		 */
		private final PacketByteBuf data;
		/**
		 * A channel which will receive a {@link NetworkEvent.LoginPayloadEvent} from the {@link FMLLoginWrapper}.
		 */
		private final Identifier channelName;

		/**
		 * Some context for logging purposes.
		 */
		private final String messageContext;

		public LoginPayload(final PacketByteBuf buffer, final Identifier channelName, final String messageContext) {
			this.data = buffer;
			this.channelName = channelName;
			this.messageContext = messageContext;
		}

		public PacketByteBuf getData() {
			return data;
		}

		public Identifier getChannelName() {
			return channelName;
		}

		public String getMessageContext() {
			return messageContext;
		}
	}

	/**
	 * Builder for constructing network channels using a builder style API.
	 */
	public static class ChannelBuilder {
		private Identifier channelName;
		private Supplier<String> networkProtocolVersion;
		private Predicate<String> clientAcceptedVersions;
		private Predicate<String> serverAcceptedVersions;

		/**
		 * The name of the channel. Must be unique.
		 * @param channelName The name of the channel
		 * @return the channel builder
		 */
		public static ChannelBuilder named(Identifier channelName) {
			ChannelBuilder builder = new ChannelBuilder();
			builder.channelName = channelName;
			return builder;
		}

		/**
		 * The network protocol string for this channel. This will be gathered during login and sent to
		 * the remote partner, where it will be tested with against the relevant predicate.
		 *
		 * @see #serverAcceptedVersions(Predicate)
		 * @see #clientAcceptedVersions(Predicate)
		 * @param networkProtocolVersion A supplier of strings for network protocol version testing
		 * @return the channel builder
		 */
		public ChannelBuilder networkProtocolVersion(Supplier<String> networkProtocolVersion) {
			this.networkProtocolVersion = networkProtocolVersion;
			return this;
		}

		/**
		 * A predicate run on the client, with the {@link #networkProtocolVersion(Supplier)} string from
		 * the server, or the special value {@link NetworkRegistry#ABSENT} indicating the absence of
		 * the channel on the remote side.
		 * @param clientAcceptedVersions A predicate for testing
		 * @return the channel builder
		 */
		public ChannelBuilder clientAcceptedVersions(Predicate<String> clientAcceptedVersions) {
			this.clientAcceptedVersions = clientAcceptedVersions;
			return this;
		}

		/**
		 * A predicate run on the server, with the {@link #networkProtocolVersion(Supplier)} string from
		 * the server, or the special value {@link NetworkRegistry#ABSENT} indicating the absence of
		 * the channel on the remote side.
		 * @param serverAcceptedVersions A predicate for testing
		 * @return the channel builder
		 */
		public ChannelBuilder serverAcceptedVersions(Predicate<String> serverAcceptedVersions) {
			this.serverAcceptedVersions = serverAcceptedVersions;
			return this;
		}

		/**
		 * Create the network channel.
		 * @return the {@link ListenableChannel} allowing registration of listeners
		 */
		private ListenableChannel createNetworkChannel() {
			return createChannel(channelName, networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions);
		}

		/**
		 * Build a new {@link SimpleChannel} with this builder's configuration.
		 *
		 * @return A new {@link SimpleChannel}
		 */
		public SimpleChannel simpleChannel() {
			return new SimpleChannel(channelName, createNetworkChannel());
		}

		/**
		 * Build a new {@link EventNetworkChannel} with this builder's configuration.
		 * @return A new {@link EventNetworkChannel}
		 */
		public EventNetworkChannel eventNetworkChannel() {
			return new EventNetworkChannel(createNetworkChannel());
		}
	}
}
