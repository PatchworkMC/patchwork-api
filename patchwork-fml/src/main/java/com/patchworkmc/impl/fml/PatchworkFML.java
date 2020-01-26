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

package com.patchworkmc.impl.fml;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSidedProvider;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.event.server.ServerStopCallback;
import net.fabricmc.loader.api.FabricLoader;

public class PatchworkFML implements ModInitializer {
	@Override
	public void onInitialize() {
		ServerStartCallback.EVENT.register(server -> LogicalSidedProvider.setServer(() -> server));
		ServerStopCallback.EVENT.register(server -> LogicalSidedProvider.setServer(null));

		Object instance = FabricLoader.getInstance().getGameInstance();

		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> LogicalSidedProvider.setClient(() -> (MinecraftClient) instance));
		DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> LogicalSidedProvider.setServer(() -> (MinecraftServer) instance));
	}
}
