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

package net.minecraftforge.fml.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.network.ClientConnection;

import net.patchworkmc.impl.networking.ClientNetworkingEvents;

public class ClientHooks {
	public static void firePlayerLogin(final ClientPlayerInteractionManager interactionManager, final ClientPlayerEntity player, final ClientConnection clientConnection) {
		ClientNetworkingEvents.firePlayerLogin(interactionManager, player, clientConnection);
	}

	public static void firePlayerLogout(final ClientPlayerInteractionManager interactionManager, final ClientPlayerEntity player) {
		ClientNetworkingEvents.firePlayerLogout(interactionManager, player);
	}

	public static void firePlayerRespawn(final ClientPlayerInteractionManager interactionManager, final ClientPlayerEntity oldPlayer, final ClientPlayerEntity newPlayer, final ClientConnection clientConnection) {
		ClientNetworkingEvents.firePlayerRespawn(interactionManager, oldPlayer, newPlayer, clientConnection);
	}
}
