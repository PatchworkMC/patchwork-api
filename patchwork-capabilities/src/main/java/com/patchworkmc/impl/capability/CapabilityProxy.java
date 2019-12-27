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

package com.patchworkmc.impl.capability;

import net.minecraftforge.common.capabilities.CapabilityProvider;

/**
 * Since some classes don't actually extend {@link CapabilityProvider}
 */
public interface CapabilityProxy {

	@SuppressWarnings("unchecked")
	static <T> CapabilityProvider<T> getProvider(Object object) {
		if (object == null) {
			return null;
		}

		if (object instanceof CapabilityProviderInterface) {
			return (CapabilityProvider<T>) ((CapabilityProviderInterface) object).getCapabilityProvider$impl();
		}

		return (CapabilityProvider<T>) object;
	}
}
