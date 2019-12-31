package com.patchworkmc.api.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.eventbus.api.GenericEvent;

/**
 * Used for hooking {@link net.minecraftforge.common.capabilities.CapabilityInject} via a synthetic handler
 *
 * @param <T> The capability type
 */
public class CapabilityRegisteredEvent<T> extends GenericEvent<T> {

	public final Capability<T> capability;

	// For EventBus
	public CapabilityRegisteredEvent() {
		this(null, null);
	}

	public CapabilityRegisteredEvent(Class<T> type, Capability<T> capability) {
		super(type);
		this.capability = capability;
	}
}
