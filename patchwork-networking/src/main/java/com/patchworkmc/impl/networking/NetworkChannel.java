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

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraftforge.fml.network.ICustomPacket;
import net.minecraftforge.fml.network.NetworkEvent;

import net.minecraft.util.Identifier;

public class NetworkChannel implements NamedChannel, ListenableChannel, VersionedChannel {
	private final Identifier name;

	private BiConsumer<ICustomPacket<?>, NetworkEvent.Context> packetListener;
	private Consumer<NetworkEvent.ChannelRegistrationChangeEvent> registrationChangeListener;
	private Consumer<NetworkEvent.GatherLoginPayloadsEvent> gatherLoginPayloadsListener;

	private final String networkProtocolVersion;
	private final Predicate<String> clientAcceptedVersions;
	private final Predicate<String> serverAcceptedVersions;

	public NetworkChannel(Identifier name, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
		this.name = name;
		this.networkProtocolVersion = networkProtocolVersion.get();
		this.clientAcceptedVersions = clientAcceptedVersions;
		this.serverAcceptedVersions = serverAcceptedVersions;
	}

	@Override
	public Identifier getChannelName() {
		return name;
	}

	@Override
	public void setPacketListener(BiConsumer<ICustomPacket<?>, NetworkEvent.Context> listener) {
		this.packetListener = listener;
	}

	@Override
	public void setRegistrationChangeListener(Consumer<NetworkEvent.ChannelRegistrationChangeEvent> listener) {
		this.registrationChangeListener = listener;
	}

	@Override
	public void setGatherLoginPayloadsListener(Consumer<NetworkEvent.GatherLoginPayloadsEvent> listener) {
		this.gatherLoginPayloadsListener = listener;
	}

	public void onPacket(ICustomPacket<?> packet, NetworkEvent.Context context) {
		if (packetListener != null) {
			packetListener.accept(packet, context);
		}
	}

	public void onRegistrationChange(NetworkEvent.ChannelRegistrationChangeEvent event) {
		if (registrationChangeListener != null) {
			registrationChangeListener.accept(event);
		}
	}

	public void onGatherLoginPayloads(NetworkEvent.GatherLoginPayloadsEvent event) {
		if (gatherLoginPayloadsListener != null) {
			gatherLoginPayloadsListener.accept(event);
		}
	}

	@Override
	public String getNetworkProtocolVersion() {
		return networkProtocolVersion;
	}

	@Override
	public boolean tryServerVersionOnClient(final String serverVersion) {
		return this.clientAcceptedVersions.test(serverVersion);
	}

	@Override
	public boolean tryClientVersionOnServer(final String clientVersion) {
		return this.serverAcceptedVersions.test(clientVersion);
	}
}
