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

package net.patchworkmc.impl.event.render;

import java.util.function.Consumer;

import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.Event;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;

public class RenderEvents {
	private static Consumer<Event> eventDispatcher;

	public static void registerEventDispatcher(Consumer<Event> dispatcher) {
		eventDispatcher = dispatcher;
	}

	public static void onBlockColorsInit(BlockColors blockColors) {
		eventDispatcher.accept(new ColorHandlerEvent.Block(blockColors));
	}

	public static void onItemColorsInit(ItemColors itemColors, BlockColors blockColors) {
		eventDispatcher.accept(new ColorHandlerEvent.Item(itemColors, blockColors));
	}
}
