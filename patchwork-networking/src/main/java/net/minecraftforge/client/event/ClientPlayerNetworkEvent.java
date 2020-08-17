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

package net.minecraftforge.client.event;

import net.minecraftforge.eventbus.api.Event;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.network.ClientConnection;

/**
 * Client-side events fired on changes to player connectivity.
 */
public class ClientPlayerNetworkEvent extends Event {
	private final ClientPlayerInteractionManager playerInteractionManager;
	private final ClientPlayerEntity player;
	private final ClientConnection clientConnection;

	ClientPlayerNetworkEvent(final ClientPlayerInteractionManager interactionManager, final ClientPlayerEntity player, final ClientConnection clientConnection) {
		this.playerInteractionManager = interactionManager;
		this.player = player;
		this.clientConnection = clientConnection;
	}

	/**
	 * @return The ClientPlayerInteractionManager instance for the client side.
	 */
	public ClientPlayerInteractionManager getController() {
		return this.playerInteractionManager;
	}

	/**
	 * @return The player instance, if present (otherwise, returns null).
	 */
	public ClientPlayerEntity getPlayer() {
		return this.player;
	}

	/**
	 * @return The network connection, if present (otherwise, returns null).
	 */
	public ClientConnection getNetworkManager() {
		return this.clientConnection;
	}

	/**
	 * Fired when the player logs out.
	 *
	 * <p>Note: This might also be fired when a new integrated server is being created.</p>
	 */
	public static class LoggedInEvent extends ClientPlayerNetworkEvent {
		public LoggedInEvent(final ClientPlayerInteractionManager interactionManager, final ClientPlayerEntity player, final ClientConnection clientConnection) {
			super(interactionManager, player, clientConnection);
		}
	}

	/**
	 * Fired when the player logs out.
	 *
	 * <p>Note: This might also fire when a new integrated server is being created.</p>
	 */
	public static class LoggedOutEvent extends ClientPlayerNetworkEvent {
		public LoggedOutEvent(final ClientPlayerInteractionManager interactionManager, final ClientPlayerEntity player, final ClientConnection clientConnection) {
			super(interactionManager, player, clientConnection);
		}
	}

	/**
	 * Fired when the player object respawns, such as dimension changes.
	 */
	public static class RespawnEvent extends ClientPlayerNetworkEvent {
		private final ClientPlayerEntity oldPlayer;

		public RespawnEvent(final ClientPlayerInteractionManager interactionManager, final ClientPlayerEntity oldPlayer, final ClientPlayerEntity newPlayer, final ClientConnection clientConnection) {
			super(interactionManager, newPlayer, clientConnection);
			this.oldPlayer = oldPlayer;
		}

		public ClientPlayerEntity getOldPlayer() {
			return this.oldPlayer;
		}

		public ClientPlayerEntity getNewPlayer() {
			return super.getPlayer();
		}
	}
}
