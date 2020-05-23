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

import java.util.function.Consumer;

import net.minecraftforge.fml.ModContainer;

/**
 * This is the first of four commonly called events during mod initialization.
 *
 * <p>Called before {@link FMLClientSetupEvent} or {@link FMLDedicatedServerSetupEvent} during mod startup.</p>
 *
 * <p>Called after {@link net.minecraftforge.event.RegistryEvent.Register} events have been fired.</p>
 *
 * <p>Either register your listener using {@link net.minecraftforge.fml.AutomaticEventSubscriber} and
 * {@link net.minecraftforge.eventbus.api.SubscribeEvent} or
 * {@link net.minecraftforge.eventbus.api.IEventBus#addListener(Consumer)} in your constructor.</p>
 *
 * <p>Most non-specific mod setup will be performed here. Note that this is a parallel dispatched event - you cannot
 * interact with game state in this event.</p>
 *
 * @see net.minecraftforge.fml.DeferredWorkQueue to enqueue work to run on the main game thread after this event has
 * completed dispatch
 */
public class FMLCommonSetupEvent extends ModLifecycleEvent {
	public FMLCommonSetupEvent(final ModContainer container) {
		super(container);
	}
}
