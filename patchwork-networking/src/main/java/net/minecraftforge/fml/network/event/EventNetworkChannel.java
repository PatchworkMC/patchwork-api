package net.minecraftforge.fml.network.event;

import java.util.function.Consumer;

import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkInstance;

public class EventNetworkChannel
{
	private final NetworkInstance instance;

	public EventNetworkChannel(NetworkInstance instance)
	{
		this.instance = instance;
	}

	public <T extends NetworkEvent> void addListener(Consumer<T> eventListener)
	{
		instance.addListener(eventListener);
	}

	public void registerObject(Object object)
	{
		instance.registerObject(object);
	}

	public void unregisterObject(Object object)
	{
		instance.unregisterObject(object);
	}
}
