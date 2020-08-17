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

package net.patchworkmc.impl.networking;

import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.network.ClientConnection;

public class ClientNetworkingEvents {
	public static void firePlayerLogin(final ClientPlayerInteractionManager interactionManager, final ClientPlayerEntity player, final ClientConnection clientConnection) {
		MinecraftForge.EVENT_BUS.post(new ClientPlayerNetworkEvent.LoggedInEvent(interactionManager, player, clientConnection));
	}

	public static void firePlayerLogout(final ClientPlayerInteractionManager interactionManager, final ClientPlayerEntity player) {
		MinecraftForge.EVENT_BUS.post(new ClientPlayerNetworkEvent.LoggedOutEvent(interactionManager, player, player != null ? player.networkHandler != null ? player.networkHandler.getConnection() : null : null));
	}

	public static void firePlayerRespawn(final ClientPlayerInteractionManager interactionManager, final ClientPlayerEntity oldPlayer, final ClientPlayerEntity newPlayer, final ClientConnection clientConnection) {
		MinecraftForge.EVENT_BUS.post(new ClientPlayerNetworkEvent.RespawnEvent(interactionManager, oldPlayer, newPlayer, clientConnection));
	}
}
