package com.patchworkmc.impl.event.entity;

import net.minecraft.util.ActionResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;

public class EntityEvents implements ModInitializer {
	private static final Logger LOGGER = LogManager.getLogger("patchwork-events-entity");

	@Override
	public void onInitialize() {
		UseItemCallback.EVENT.register((player, world, hand) -> {
			// TODO: Check for SPECTATOR game mode

			if (player.getItemCooldownManager().isCoolingDown(player.getStackInHand(hand).getItem())) {
				return ActionResult.PASS;
			}

			PlayerInteractEvent.RightClickItem event = new PlayerInteractEvent.RightClickItem(player, hand);

			MinecraftForge.EVENT_BUS.post(event);

			if(event.isCanceled() && event.getCancellationResult() == ActionResult.PASS) {
				// TODO: Fabric API doesn't have a way to express "cancelled, but return PASS"

				LOGGER.error("[patchwork-events-entity] RightClickItem: Cannot cancel with a result of PASS yet, assuming SUCCESS");

				return ActionResult.SUCCESS;
			}

			return event.getCancellationResult();
		});

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			// TODO: Check for SPECTATOR game mode

			PlayerInteractEvent.RightClickBlock event = new PlayerInteractEvent.RightClickBlock(player, hand, hitResult.getBlockPos(), hitResult.getSide());

			MinecraftForge.EVENT_BUS.post(event);

			if(event.isCanceled()) {
				if(event.getCancellationResult() == ActionResult.PASS) {
					// TODO: Fabric API doesn't have a way to express "cancelled, but return PASS"
					LOGGER.error("[patchwork-events-entity] RightClickBlock: Cannot cancel with a result of PASS yet, assuming SUCCESS");

					return ActionResult.SUCCESS;
				} else {
					return event.getCancellationResult();
				}
			}

			// Not cancelled entirely, but a single behavior is cancelled.

			if(event.getUseBlock() == Event.Result.DENY || event.getUseItem() == Event.Result.DENY) {
				// TODO: Handle Result.DENY -> ActionResult.PASS

				throw new UnsupportedOperationException("Cannot handle partial RightClickBlock cancellation yet");
			}

			return ActionResult.PASS;
		});
	}
}
