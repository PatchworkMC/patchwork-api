package com.patchworkmc.impl.event.entity;

import net.minecraft.util.ActionResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;

public class EntityEvents implements ModInitializer {
	@Override
	public void onInitialize() {
		UseItemCallback.EVENT.register((player, world, hand) -> {
			// TODO: Check for SPECTATOR game mode

			if (player.getItemCooldownManager().isCoolingDown(player.getStackInHand(hand).getItem())) {
				return ActionResult.PASS;
			}

			PlayerInteractEvent.RightClickItem event = new PlayerInteractEvent.RightClickItem(player, hand);

			MinecraftForge.EVENT_BUS.post(event);

			return event.getCancellationResult();
		});

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			// TODO: Check for SPECTATOR game mode

			PlayerInteractEvent.RightClickBlock event = new PlayerInteractEvent.RightClickBlock(player, hand, hitResult.getBlockPos(), hitResult.getSide());

			MinecraftForge.EVENT_BUS.post(event);

			if(event.getUseBlock() == Event.Result.DENY || event.getUseItem() == Event.Result.DENY) {
				// TODO: Handle Result.DENY -> ActionResult.PASS

				throw new UnsupportedOperationException("Cannot handle a DENY result yet");
			}

			return event.getCancellationResult();
		});
	}
}
