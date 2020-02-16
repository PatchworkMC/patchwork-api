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

package net.minecraftforge.fml.javafmlmod;

import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.IEventListener;
import net.minecraftforge.fml.ModContainer;

public class FMLModContainer extends ModContainer {
	private final IEventBus eventBus;

	public FMLModContainer(String id) {
		super(id);

		this.eventBus = BusBuilder.builder().setExceptionHandler(this::onEventFailed).setTrackPhases(false).build();
	}

	public FMLModContainer(String id, IEventBus bus) {
		super(id);

		this.eventBus = bus;
	}

	public IEventBus getEventBus() {
		return eventBus;
	}

	private void onEventFailed(IEventBus iEventBus, Event event, IEventListener[] listeners, int i, Throwable throwable) {
		// TODO
	}
}
