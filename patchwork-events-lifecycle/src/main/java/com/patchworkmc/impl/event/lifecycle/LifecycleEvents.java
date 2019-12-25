package com.patchworkmc.impl.event.lifecycle;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.world.WorldTickCallback;

public class LifecycleEvents implements ModInitializer {
	@Override
	public void onInitialize() {
		WorldTickCallback.EVENT.register(world -> fireWorldTickEvent(TickEvent.Phase.END, world));
	}

	public static void fireWorldTickEvent(TickEvent.Phase phase, World world) {
		LogicalSide side = world.isClient() ? LogicalSide.CLIENT : LogicalSide.SERVER;
		TickEvent.WorldTickEvent event = new TickEvent.WorldTickEvent(side, phase, world);

		MinecraftForge.EVENT_BUS.post(event);
	}

	public static void onPlayerPreTick(PlayerEntity player) {
		MinecraftForge.EVENT_BUS.post(new TickEvent.PlayerTickEvent(TickEvent.Phase.START, player));
	}

	public static void onPlayerPostTick(PlayerEntity player) {
		MinecraftForge.EVENT_BUS.post(new TickEvent.PlayerTickEvent(TickEvent.Phase.END, player));
	}
}
