package com.patchworkmc.impl.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
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
			if(player.isSpectator()) {
				return ActionResult.PASS;
			}

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
			if(player.isSpectator()) {
				return ActionResult.PASS;
			}

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

		// TODO: Note: UseEntityCallback is closer to EntityInteractSpecific. We're on our own for EntityInteract.
	}

	public static ActionResult onInteractEntity(PlayerEntity player, Entity entity, Hand hand) {
		PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(player, hand, entity);

		System.out.println(player.getEntityWorld().isClient + "|onInteractEntity");

		MinecraftForge.EVENT_BUS.post(event);

		return event.isCanceled() ? event.getCancellationResult() : null;
	}

	public static boolean onLivingDeath(LivingEntity entity, DamageSource src) {
		return MinecraftForge.EVENT_BUS.post(new LivingDeathEvent(entity, src));
	}
}
