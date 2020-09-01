/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2020, 2019-2020
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.patchworkmc.impl.capability;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.patchworkmc.api.capability.CapabilityRegisteredCallback;

public class CapabilityRegisteredCallbackInternal {
	private static final Logger LOGGER = LogManager.getLogger(CapabilityRegisteredCallback.class);

	private static final Map<String, Event<?>> CALLBACKS = new HashMap<>();

	@SuppressWarnings("unchecked")
	public static <C> Event<CapabilityRegisteredCallback<C>> getOrCreateEvent(String type) {
		return (Event<CapabilityRegisteredCallback<C>>) CALLBACKS.computeIfAbsent(type, $ -> EventFactory.createArrayBacked(CapabilityRegisteredCallback.class, capability -> { }, callbacks -> capability -> {
			boolean error = false;
			Throwable throwable = new Throwable();

			for (CapabilityRegisteredCallback<C> callback : callbacks) {
				try {
					callback.onCapabilityRegistered(capability);
				} catch (Throwable thr) {
					error = true;
					throwable.addSuppressed(thr);
				}
			}

			if (error) {
				LOGGER.error("An uncaught exception was thrown while processing a CapabilityRegisteredCallback<{}>", type, throwable);
			}
		}));
	}
}
