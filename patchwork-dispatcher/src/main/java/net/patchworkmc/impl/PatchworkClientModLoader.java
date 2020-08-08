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

package net.patchworkmc.impl;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.ClientBuiltinResourcePackProvider;
import net.minecraft.client.resource.ClientResourcePackProfile;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourcePackManager;

public class PatchworkClientModLoader {
	public static void begin(final MinecraftClient minecraft, final ResourcePackManager<ClientResourcePackProfile> defaultResourcePacks,
						final ReloadableResourceManager mcResourceManager, ClientBuiltinResourcePackProvider metadataSerializer) {
		Patchwork.gatherAndInitializeMods();
		Patchwork.loadMods(container -> new FMLClientSetupEvent(() -> minecraft, container), PatchworkClientModLoader::preSidedRunnable, PatchworkClientModLoader::postSidedRunnable);
		Patchwork.finishMods();
	}

	private static void preSidedRunnable(Consumer<Supplier<Event>> perModContainerEventProcessor) {
		perModContainerEventProcessor.accept(ModelRegistryEvent::new);
	}

	private static void postSidedRunnable(Consumer<Supplier<Event>> perModContainerEventProcessor) {
	}
}
