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

import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.Direction;

public interface ICapabilityProvider {
	/**
	 * Retrieves the {@link LazyOptional optional} handler for the capability requested on the specific side.
	 * The return value <strong>CAN</strong> be the same for multiple faces.
	 *
	 * <p>Modders are encouraged to cache this value, using the listener capabilities of the optional to
	 * be notified if the requested capability get lost.
	 *
	 * @param capability The {@link Capability capability} to check
	 * @param direction  The {@link Direction direction} to check from,
	 *                   <strong>CAN BE NULL</strong>. Null is defined to represent 'internal' or 'self'
	 * @return The requested a {@link LazyOptional optional} holding the requested capability.
	 */
	@NotNull
	<T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction);

	/*
	 * Purely added as a bouncer to sided version, to make modders stop complaining about calling with a null value.
	 * This should never be OVERRIDDEN, modders should only ever implement the sided version.
	 */
	@NotNull
	default <T> LazyOptional<T> getCapability(@NotNull final Capability<T> cap) {
		return getCapability(cap, null);
	}
}
