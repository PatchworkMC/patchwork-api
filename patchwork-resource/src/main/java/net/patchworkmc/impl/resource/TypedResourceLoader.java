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

package net.patchworkmc.impl.resource;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ReloadRequirements;
import net.minecraftforge.resource.SelectiveReloadStateHandler;
import net.minecraftforge.resource.VanillaResourceType;

import net.minecraft.client.MinecraftClient;

public interface TypedResourceLoader {
	default IResourceType getResourceType() {
		return null;
	}

	/**
	 * Called by vanilla hooks and ForgeHooksClient.
	 */
	static CompletableFuture<Void> patchwork$refreshResources(MinecraftClient mc, VanillaResourceType... types) {
		SelectiveReloadStateHandler.INSTANCE.beginReload(ReloadRequirements.include(types));
		CompletableFuture<Void> ret = mc.reloadResources();
		SelectiveReloadStateHandler.INSTANCE.endReload();
		return ret;
	}

	@Nullable
	static IResourceType patchwork$getResourceType(Object obj) {
		return obj instanceof TypedResourceLoader ? ((TypedResourceLoader) obj).getResourceType() : null;
	}
}
