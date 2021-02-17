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

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class CapabilityEvents {
	// This is less restrictive than Forge's implementation, since patchwork can't make vanilla extend stuff at random.
	@SuppressWarnings("unchecked")
	@Nullable
	public static <T> CapabilityDispatcher gatherCapabilities(Class<? extends T> type, T provider, @Nullable ICapabilityProvider parent) {
		AttachCapabilitiesEvent<T> event = new AttachCapabilitiesEvent<T>((Class<T>) type, provider);
		MinecraftForge.EVENT_BUS.post(event);

		if (!event.getCapabilities().isEmpty() || parent != null) {
			return new CapabilityDispatcher(event.getCapabilities(), event.getListeners(), parent);
		} else {
			return null;
		}
	}
}
