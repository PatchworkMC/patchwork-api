package com.patchworkmc.impl.event;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;

public class EntityEvents implements ModInitializer {
	@Override
	public void onInitialize() {
		UseItemCallback.EVENT.register((player, world, hand) -> {
			PlayerInteractEvent.RightClickItem event = new PlayerInteractEvent.RightClickItem(player, hand);

			MinecraftForge.EVENT_BUS.post(event);

			return event.getCancellationResult();
		});
	}
}
