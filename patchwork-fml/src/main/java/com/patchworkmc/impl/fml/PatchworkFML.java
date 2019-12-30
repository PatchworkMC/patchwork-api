package com.patchworkmc.impl.fml;

import net.minecraftforge.fml.LogicalSidedProvider;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.event.server.ServerStopCallback;

public class PatchworkFML implements ModInitializer {
	@Override
	public void onInitialize() {
		ServerStartCallback.EVENT.register(server -> LogicalSidedProvider.setServer(() -> server));
		ServerStopCallback.EVENT.register(server -> LogicalSidedProvider.setServer(null));
	}
}
