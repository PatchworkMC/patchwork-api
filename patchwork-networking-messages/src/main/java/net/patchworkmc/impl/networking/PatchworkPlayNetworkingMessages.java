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

import java.util.function.Consumer;

import io.netty.buffer.Unpooled;
import net.minecraftforge.fml.network.FMLPlayMessages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;

public class PatchworkPlayNetworkingMessages implements ModInitializer, MessageFactory {
	public static final Logger LOGGER = LogManager.getLogger("patchwork-networking");
	public static final Identifier IDENTIFIER = new Identifier("fml", "play");
	public static final NetworkChannelVersion VERSION = new NetworkChannelVersion("FML2", version -> true, version -> true);
	public static final short SPAWN_ENTITY = 0;
	public static final short OPEN_CONTAINER = 1;

	@Override
	public void onInitialize() {
		PatchworkNetworking.getVersionManager().createChannel(IDENTIFIER, VERSION);
		PatchworkNetworking.setFactory(this);

		ServerSidePacketRegistry.INSTANCE.register(IDENTIFIER, (context, buf) -> {
			LOGGER.warn("Received an fml:play on the server, this should not happen! Kicking the offending client.");

			ServerPlayerEntity entity = (ServerPlayerEntity) context.getPlayer();

			entity.networkHandler.disconnect(new LiteralText("fml:play messages should only be sent to the client!"));
		});
	}

	@Override
	public Packet<?> getEntitySpawningPacket(Entity entity) {
		FMLPlayMessages.SpawnEntity message = new FMLPlayMessages.SpawnEntity(entity);
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

		buf.writeByte(SPAWN_ENTITY);
		FMLPlayMessages.SpawnEntity.encode(message, buf);

		return ServerSidePacketRegistry.INSTANCE.toPacket(IDENTIFIER, buf);
	}

	@Override
	public void sendContainerOpenPacket(ServerPlayerEntity player, NamedScreenHandlerFactory factory, Consumer<PacketByteBuf> extraDataWriter) {
		if (player.world.isClient) {
			return;
		}

		player.closeScreenHandler();

		ContainerSyncAccess access = (ContainerSyncAccess) player;
		int openContainerId = access.patchwork$getNewContainerSyncId();

		PacketByteBuf extraData = new PacketByteBuf(Unpooled.buffer());
		extraDataWriter.accept(extraData);

		// reset to beginning in case modders read for whatever reason
		extraData.readerIndex(0);

		PacketByteBuf output = new PacketByteBuf(Unpooled.buffer());
		output.writeVarInt(extraData.readableBytes());
		output.writeBytes(extraData);

		if (output.readableBytes() > 32600 || output.readableBytes() < 1) {
			throw new IllegalArgumentException("Invalid PacketByteBuf for openGui, found " + output.readableBytes() + " bytes");
		}

		ScreenHandler c = factory.createMenu(openContainerId, player.inventory, player);
		ScreenHandlerType<?> type = c.getType();

		FMLPlayMessages.OpenContainer msg = new FMLPlayMessages.OpenContainer(type, openContainerId, factory.getDisplayName(), output);
		Packet<?> packet = PatchworkPlayNetworkingMessages.getOpenContainerPacket(msg);

		player.networkHandler.sendPacket(packet);
		player.currentScreenHandler = c;
		player.currentScreenHandler.addListener(player);

		// TODO MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, c));
	}

	private static Packet<?> getOpenContainerPacket(FMLPlayMessages.OpenContainer message) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

		buf.writeByte(OPEN_CONTAINER);
		FMLPlayMessages.OpenContainer.encode(message, buf);

		return ServerSidePacketRegistry.INSTANCE.toPacket(IDENTIFIER, buf);
	}
}
