/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2019, 2019
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

package com.patchworkmc.api.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.eventbus.api.GenericEvent;

/**
 * Used for hooking {@link net.minecraftforge.common.capabilities.CapabilityInject} via a synthetic handler.
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
