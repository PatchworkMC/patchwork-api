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

package net.patchworkmc.impl.networking;

import java.util.Objects;
import java.util.function.Predicate;

public final class NetworkChannelVersion {
	private final String networkProtocolVersion;
	private final Predicate<String> clientAcceptedVersions;
	private final Predicate<String> serverAcceptedVersions;

	public NetworkChannelVersion(String networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
		Objects.requireNonNull(networkProtocolVersion);
		Objects.requireNonNull(clientAcceptedVersions);
		Objects.requireNonNull(serverAcceptedVersions);

		this.networkProtocolVersion = networkProtocolVersion;
		this.clientAcceptedVersions = clientAcceptedVersions;
		this.serverAcceptedVersions = serverAcceptedVersions;
	}

	public String getNetworkProtocolVersion() {
		return networkProtocolVersion;
	}

	public boolean tryServerVersionOnClient(final String serverVersion) {
		return this.clientAcceptedVersions.test(serverVersion);
	}

	public boolean tryClientVersionOnServer(final String clientVersion) {
		return this.serverAcceptedVersions.test(clientVersion);
	}
}
