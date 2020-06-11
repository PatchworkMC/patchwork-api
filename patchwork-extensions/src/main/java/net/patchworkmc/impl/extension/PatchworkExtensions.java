package net.patchworkmc.impl.extension;

import net.minecraftforge.common.util.FakePlayerFactory;

import net.minecraft.server.world.ServerWorld;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStopCallback;

public class PatchworkExtensions implements ModInitializer {
	@Override
	public void onInitialize() {
		ServerStopCallback.EVENT.register(server -> {
			for (ServerWorld world : server.getWorlds()) {
				FakePlayerFactory.unloadWorld(world);
			}
		});
	}
}
