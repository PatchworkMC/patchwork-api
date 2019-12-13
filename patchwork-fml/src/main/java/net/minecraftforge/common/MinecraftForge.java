package net.minecraftforge.common;

import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.IEventBus;

public class MinecraftForge {
	// TODO: the original source has a startShutdown here
	public static final IEventBus EVENT_BUS = BusBuilder.builder().build();
}
