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

package net.minecraftforge.event.entity.player;

import net.minecraftforge.event.entity.living.LivingEvent;

import net.minecraft.entity.player.PlayerEntity;

/**
 * PlayerEvent is fired whenever an event involving Living entities occurs.
 *
 * <p>If a method utilizes this {@link net.minecraftforge.eventbus.api.Event} as its parameter, the method will
 * receive every child event of this class.</p>
 *
 * <p>All children of this event are fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.</p>
 */
public class PlayerEvent extends LivingEvent {
	private final PlayerEntity playerEntity;

	// For EventBus
	public PlayerEvent() {
		this(null);
	}

	public PlayerEvent(PlayerEntity player) {
		super(player);
		playerEntity = player;
	}

	/**
	 * Use {@link #getPlayer()}.
	 *
	 * @return Player
	 */
	@Deprecated
	public PlayerEntity getPlayerEntity() {
		return playerEntity;
	}

	/**
	 * @return Player
	 */
	public PlayerEntity getPlayer() {
		return playerEntity;
	}

	/**
	 * Called on the server at the end of PlayerManager handling the connection.
	 */
	public static class PlayerLoggedInEvent extends PlayerEvent {
		public PlayerLoggedInEvent(PlayerEntity player) {
			super(player);
		}
	}

	/*TODO Events:
	HarvestCheck
	BreakSpeed
	NameFormat
	Clone
	StartTracking
	StopTracking
	LoadFromFile
	SaveToFile
	Visibility
	ItemPickupEvent
	ItemCraftedEvent
	ItemSmeltedEventLogg
	PlayerLoggedOutEvent
	PlayerRespawnEvent
	PlayerChangedDimensionEvent*/
}
