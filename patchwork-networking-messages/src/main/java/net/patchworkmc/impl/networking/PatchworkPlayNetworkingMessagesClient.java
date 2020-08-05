package net.patchworkmc.impl.networking;

import net.minecraftforge.fml.network.FMLPlayMessages;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;

public class PatchworkPlayNetworkingMessagesClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// TODO: Move to client initializer
		ClientSidePacketRegistry.INSTANCE.register(PatchworkPlayNetworkingMessages.IDENTIFIER, (context, buf) -> {
			int id = buf.readUnsignedByte();

			if (id == PatchworkPlayNetworkingMessages.SPAWN_ENTITY) {
				FMLPlayMessages.SpawnEntity spawn = FMLPlayMessages.SpawnEntity.decode(buf);
				FMLPlayMessages.SpawnEntity.handle(spawn, context);
			} else if (id == PatchworkPlayNetworkingMessages.OPEN_CONTAINER) {
				FMLPlayMessages.OpenContainer open = FMLPlayMessages.OpenContainer.decode(buf);
				FMLPlayMessages.OpenContainer.handle(open, context);
			} else {
				PatchworkPlayNetworkingMessages.LOGGER.warn("Received an unknown fml:play message with an id of {} and a payload of {} bytes", id, buf.readableBytes());
			}
		});
	}
}
