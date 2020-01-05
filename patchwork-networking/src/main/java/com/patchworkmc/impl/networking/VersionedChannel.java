package com.patchworkmc.impl.networking;

import com.patchworkmc.api.networking.Channel;

public interface VersionedChannel extends Channel {
	String getNetworkProtocolVersion();
	boolean tryServerVersionOnClient(final String serverVersion);
	boolean tryClientVersionOnServer(final String clientVersion);
}
