package net.patchworkmc.impl.registries;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface RegistrationCompleteCallback {
	Event<RegistrationCompleteCallback> EVENT = EventFactory.createArrayBacked(RegistrationCompleteCallback.class, listeners -> () -> {
		for (RegistrationCompleteCallback listener: listeners) {
			listener.onRegistrationComplete();
		}
	});

	void onRegistrationComplete();
}
