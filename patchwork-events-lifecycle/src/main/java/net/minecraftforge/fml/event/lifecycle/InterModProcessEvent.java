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

package net.minecraftforge.fml.event.lifecycle;

import java.util.function.Predicate;

import net.minecraftforge.fml.ModContainer;

/**
 * This is the fourth of four commonly called events during mod lifecycle startup.
 *
 * <p>Called after {@link InterModEnqueueEvent}</p>
 *
 * <p>Retrieve {@link net.minecraftforge.fml.InterModComms} {@link net.minecraftforge.fml.InterModComms.IMCMessage} suppliers
 * and process them as you wish with this event.</p>
 *
 * <p>This is a parallel dispatch event.</p>
 *
 * @see #getIMCStream()
 * @see #getIMCStream(Predicate)
 */
public class InterModProcessEvent extends ModLifecycleEvent {
	public InterModProcessEvent(final ModContainer container) {
		super(container);
	}
}
