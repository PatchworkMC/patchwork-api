package net.minecraftforge.common;

import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.IEventBus;

public class MinecraftForge {
	/**
	 * The core Forge EventBus, all events for Forge will be fired on this. You should use this to register all your
	 * listeners.
	 */
	public static final IEventBus EVENT_BUS = BusBuilder.builder().startShutdown().build();
}
