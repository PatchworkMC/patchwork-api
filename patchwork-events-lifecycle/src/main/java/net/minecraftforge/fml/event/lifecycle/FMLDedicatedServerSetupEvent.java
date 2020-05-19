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

import java.util.function.Supplier;

import net.minecraftforge.fml.ModContainer;

import net.minecraft.server.dedicated.DedicatedServer;

/**
 * This is the second of four commonly called events during mod lifecycle startup.
 *
 * <p>It is called after {@link FMLCommonSetupEvent} but before {@link InterModEnqueueEvent}.</p>
 *
 * <p>This event is only called on the dedicated game server ({@link net.minecraftforge.api.distmarker.Dist#DEDICATED_SERVER}).</p>
 *
 * <p>The client alternative is {@link FMLClientSetupEvent}.</p>
 *
 * <p>Do dedicated server specific activities with this event.</p>
 *
 * <p>This is a parallel dispatch event.</p>
 */
public class FMLDedicatedServerSetupEvent extends ModLifecycleEvent {
	private final Supplier<DedicatedServer> server;

	public FMLDedicatedServerSetupEvent(Supplier<DedicatedServer> server, ModContainer container) {
		super(container);

		this.server = server;
	}

	public Supplier<DedicatedServer> getServerSupplier() {
		return server;
	}
}
