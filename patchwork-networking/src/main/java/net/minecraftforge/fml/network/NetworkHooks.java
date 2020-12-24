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

package net.minecraftforge.fml.network;

import net.minecraft.entity.Entity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;

import net.patchworkmc.impl.networking.ListenableChannel;
import net.patchworkmc.impl.networking.MessageFactory;
import net.patchworkmc.impl.networking.PatchworkNetworking;

public class NetworkHooks {
	public static boolean onCustomPayload(final ICustomPacket<?> packet, final ClientConnection connection) {
		ListenableChannel target = NetworkRegistry.findListener(packet.getName());

		if (target == null) {
			return false;
		}

		final NetworkEvent.Context context = new NetworkEvent.Context(connection, packet.getDirection(), packet.getIndex());

		target.onPacket(packet, context);

		return context.getPacketHandled();
	}

	public static Packet<?> getEntitySpawningPacket(Entity entity) {
		MessageFactory factory = PatchworkNetworking.getMessageFactory();

		return factory.getEntitySpawningPacket(entity);
	}

	/**
	 * Request to open a GUI on the client, from the server
	 *
	 * <p>The {@link ScreenHandlerType} for the container must be registered on both sides, it handles the creation of the container on the client.
	 *
	 * @param player   The player to open the GUI for
	 * @param provider Provides the container name and allows creation of new container instances
	 */
	public static void openGui(ServerPlayerEntity player, NamedScreenHandlerFactory provider) {
		// TODO: IForgeContainerType
		player.openHandledScreen(provider);
	}

	/*TODO
	public static void openGui(ServerPlayerEntity player, NameableContainerProvider provider, BlockPos pos) {
		openGui(player, provider, buf -> buf.writeBlockPos(pos));
	}

	public static void openGui(ServerPlayerEntity player, NameableContainerProvider provider, Consumer<PacketByteBuf> extraDataWriter) {
		MessageFactory factory = PatchworkNetworking.getMessageFactory();

		factory.sendContainerOpenPacket(player, provider, extraDataWriter);
	}*/
}
