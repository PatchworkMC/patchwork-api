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

package net.minecraftforge.event.entity.player;

import net.minecraftforge.event.entity.living.LivingEvent;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;

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
	 * Called on the server at the end of {@link net.minecraft.server.PlayerManager#onPlayerConnect(net.minecraft.network.ClientConnection, net.minecraft.server.network.ServerPlayerEntity)}
	 * when the player has finished logging in.
	 */
	public static class PlayerLoggedInEvent extends PlayerEvent {
		public PlayerLoggedInEvent(PlayerEntity player) {
			super(player);
		}
	}

	/**
	 * Fired when an Entity is started to be "tracked" by this player (the player receives updates about this entity, e.g. motion).
	 */
	public static class StartTracking extends PlayerEvent {
		private final Entity target;

		public StartTracking(PlayerEntity player, Entity target) {
			super(player);
			this.target = target;
		}

		public Entity getTarget() {
			return target;
		}
	}

	/**
	 * Fired when an Entity is started to be "tracked" by this player (the player receives updates about this entity, e.g. motion).
	 */
	public static class StopTracking extends PlayerEvent {
		private final Entity target;

		public StopTracking(PlayerEntity player, Entity target) {
			super(player);
			this.target = target;
		}

		public Entity getTarget() {
			return target;
		}
	}

	/*
	 * Fired when the EntityPlayer is cloned, typically caused by the network sending a RESPAWN_PLAYER event.
	 * Either caused by death, or by traveling from the End to the overworld.
	 */
	public static class Clone extends PlayerEvent {
		private final PlayerEntity original;
		private final boolean wasDeath;

		public Clone(PlayerEntity newPlayer, PlayerEntity oldPlayer, boolean wasDeath) {
			super(newPlayer);
			this.original = oldPlayer;
			this.wasDeath = wasDeath;
		}

		/**
		 * @return The old EntityPlayer that this new entity is a clone of.
		 */
		public PlayerEntity getOriginal() {
			return original;
		}

		/**
		 * True if this event was fired because the player died.
		 * False if it was fired because the entity switched dimensions.
		 *
		 * @return Whether this event was caused by the player dying.
		 */
		public boolean isWasDeath() {
			return wasDeath;
		}
	}

	/*TODO Events:
	HarvestCheck
	BreakSpeed
	NameFormat
	LoadFromFile
	SaveToFile
	Visibility
	ItemPickupEvent
	ItemCraftedEvent
	ItemSmeltedEvent
	PlayerLoggedOutEvent
	PlayerRespawnEvent
	PlayerChangedDimensionEvent*/
}
