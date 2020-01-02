package net.minecraftforge.fml.network;

import net.minecraftforge.fml.network.ICustomPacket;
import net.minecraftforge.fml.network.NetworkRegistry;

import net.minecraft.network.ClientConnection;

public class NetworkHooks {
	public static boolean onCustomPayload(final ICustomPacket<?> packet, final ClientConnection connection) {
		return NetworkRegistry.findTarget(packet.getName()).
			map(ni->ni.dispatch(packet.getDirection(), packet, connection)).orElse(Boolean.FALSE);
	}

}
