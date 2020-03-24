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

package net.minecraftforge.fml.network.event;

import java.util.List;
import java.util.function.Consumer;

import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.IEventListener;
import net.minecraftforge.fml.network.ICustomPacket;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;

import net.patchworkmc.impl.networking.ListenableChannel;

public class EventNetworkChannel implements ListenableChannel {
	private final IEventBus networkEventBus;

	public EventNetworkChannel() {
		this.networkEventBus = BusBuilder.builder().setExceptionHandler(this::handleError).build();

		// TODO: Login packet stuff, registration change listeners
		throw new UnsupportedOperationException("Registration change / gather login payload events aren't supported");
	}

	private void handleError(IEventBus bus, Event event, IEventListener[] listeners, int i, Throwable throwable) {
		// Forge: NO-OP
	}

	@Override
	public void onPacket(final ICustomPacket<?> packet, final NetworkEvent.Context context) {
		this.networkEventBus.post(packet.getDirection().getEvent(packet, () -> context));
	}

	@Override
	public void onRegistrationChange(NetworkEvent.ChannelRegistrationChangeEvent event) {
		this.networkEventBus.post(event);
	}

	@Override
	public void onGatherLoginPayloads(List<NetworkRegistry.LoginPayload> payloads, boolean isLocal) {
		this.networkEventBus.post(new NetworkEvent.GatherLoginPayloadsEvent(payloads, isLocal));
	}

	public <T extends NetworkEvent> void addListener(Consumer<T> eventListener) {
		this.networkEventBus.addListener(eventListener);
	}

	public void registerObject(Object object) {
		this.networkEventBus.register(object);
	}

	public void unregisterObject(Object object) {
		this.networkEventBus.unregister(object);
	}
}
