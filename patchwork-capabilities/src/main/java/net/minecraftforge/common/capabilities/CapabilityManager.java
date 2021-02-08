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

package net.minecraftforge.common.capabilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

import net.minecraftforge.fml.Logging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.patchworkmc.api.capability.CapabilityRegisteredCallback;

public enum CapabilityManager {
	INSTANCE;

	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<String, Capability<?>> providers = new HashMap<>();

	/**
	 * Registers a capability to be consumed by others.
	 * APIs who define the capability should call this.
	 * To retrieve the Capability instance, use the @CapabilityInject annotation.
	 * This method is safe to call during parallel mod loading.
	 *
	 * @param type    The Interface to be registered
	 * @param storage A default implementation of the storage handler.
	 * @param factory A Factory that will produce new instances of the default implementation.
	 */
	public <T> void register(Class<T> type, Capability.IStorage<T> storage, Callable<? extends T> factory) {
		Objects.requireNonNull(type, "Attempted to register a capability with invalid type");
		Objects.requireNonNull(storage, "Attempted to register a capability with no storage implementation");
		Objects.requireNonNull(factory, "Attempted to register a capability with no default implementation factory");
		String realName = type.getName();
		Capability<T> cap;

		synchronized (providers) {
			if (providers.containsKey(realName)) {
				LOGGER.error(Logging.CAPABILITIES, "Cannot register a capability implementation multiple times : {}", realName);
				throw new IllegalArgumentException("Cannot register a capability implementation multiple times : " + realName);
			}

			cap = new Capability<>(realName, storage, factory);
			providers.put(realName, cap);
		}

		CapabilityRegisteredCallback.event(type).invoker().onCapabilityRegistered(cap);
	}
}
