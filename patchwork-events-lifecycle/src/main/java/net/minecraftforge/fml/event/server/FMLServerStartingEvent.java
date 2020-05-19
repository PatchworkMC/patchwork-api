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

package net.minecraftforge.fml.event.server;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

/**
 * Called after {@link FMLServerAboutToStartEvent} and before {@link FMLServerStartedEvent}.
 * This event allows for customizations of the server, such as loading custom commands, perhaps customizing recipes or
 * other activities.
 *
 * <p>This event is fired within {@link net.minecraft.server.dedicated.MinecraftDedicatedServer#setupServer()} and
 * {@link net.minecraft.server.integrated.IntegratedServer#setupServer()}, right before they return control
 * to {@link MinecraftServer#run()} to complete the server's startup. It is not fired if the startup has already
 * failed (ie, if setupServer has already returned false).</p>
 *
 * @author cpw
 */
public class FMLServerStartingEvent extends ServerLifecycleEvent {
	public FMLServerStartingEvent(final MinecraftServer server) {
		super(server);
	}

	public CommandDispatcher<ServerCommandSource> getCommandDispatcher() {
		return server.getCommandManager().getDispatcher();
	}
}
