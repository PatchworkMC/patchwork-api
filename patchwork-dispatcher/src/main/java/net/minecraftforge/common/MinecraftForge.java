package net.minecraftforge.common;

import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.IEventBus;

public class MinecraftForge {
	public static final IEventBus EVENT_BUS = BusBuilder.builder().startShutdown().build();
}
