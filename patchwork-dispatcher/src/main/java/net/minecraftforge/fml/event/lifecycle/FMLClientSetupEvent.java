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

import net.minecraft.client.MinecraftClient;

/**
 * This is the second of four commonly called events during mod lifecycle startup.
 *
 * <p>It is called after {@link FMLCommonSetupEvent} but before {@link InterModEnqueueEvent}.</p>
 *
 * <p>This event is only called on the game client ({@link net.minecraftforge.api.distmarker.Dist#CLIENT}).</p>
 *
 * <p>The dedicated server alternative is {@link FMLDedicatedServerSetupEvent}.</p>
 *
 * <p>Do client only setup with this event, such as {@link net.minecraft.client.options.KeyBinding}s and rendering.</p>
 *
 * <p>This is a parallel dispatch event.</p>
 */
public class FMLClientSetupEvent extends ModLifecycleEvent {
	private final Supplier<MinecraftClient> client;

	public FMLClientSetupEvent(Supplier<MinecraftClient> client, ModContainer container) {
		super(container);

		this.client = client;
	}

	public Supplier<MinecraftClient> getMinecraftSupplier() {
		return client;
	}
}
