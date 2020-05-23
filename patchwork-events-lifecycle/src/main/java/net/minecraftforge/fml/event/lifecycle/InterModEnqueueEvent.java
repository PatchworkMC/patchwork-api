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

import net.minecraftforge.fml.ModContainer;

/**
 * This is the third of four commonly called events during mod lifecycle startup.
 *
 * <p>Called before {@link InterModProcessEvent}</p>
 *
 * <p>Called after {@link FMLClientSetupEvent} or {@link FMLDedicatedServerSetupEvent}</p>
 *
 * <p>Enqueue {@link net.minecraftforge.fml.InterModComms} messages to other mods with this event.</p>
 *
 * <p>This is a parallel dispatch event.</p>
 */
public class InterModEnqueueEvent extends ModLifecycleEvent {
	public InterModEnqueueEvent(final ModContainer container) {
		super(container);
	}
}
