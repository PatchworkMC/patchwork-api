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

package com.patchworkmc.impl.networking;

import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;

public class PatchworkNetworkingMessages implements ModInitializer {
	private static final String VERSION = "FML2";
	private static final Identifier PLAY_IDENTIFIER = new Identifier("fml", "play");
	private static SimpleChannel play;

	@Override
	public void onInitialize() {
		play = NetworkRegistry.ChannelBuilder
				.named(PLAY_IDENTIFIER)
				.clientAcceptedVersions(version -> true)
				.serverAcceptedVersions(version -> true)
				.networkProtocolVersion(() -> VERSION)
				.simpleChannel();

		play.messageBuilder(FMLPlayMessages.SpawnEntity.class, 0).
				decoder(FMLPlayMessages.SpawnEntity::decode).
				encoder(FMLPlayMessages.SpawnEntity::encode).
				consumer(FMLPlayMessages.SpawnEntity::handle).
				add();
	}

	public static SimpleChannel getPlayChannel() {
		return play;
	}
}
