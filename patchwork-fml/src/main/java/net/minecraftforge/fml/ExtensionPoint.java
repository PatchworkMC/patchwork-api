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

package net.minecraftforge.fml;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class ExtensionPoint<T> {
	public static final ExtensionPoint<BiFunction<MinecraftClient, Screen, Screen>> CONFIGGUIFACTORY = new ExtensionPoint<>();
	// TODO: Not used by any Forge code, ModFileResourcePack is not implemented in Patchwork API
	// public static final ExtensionPoint<BiFunction<MinecraftClient, ModFileResourcePack, ResourcePack>> RESOURCEPACK = new ExtensionPoint<>();
	/**
	 * Compatibility display test for the mod. Used for displaying compatibility
	 * with remote servers with the same mod, and on disk saves.
	 *
	 * <p>The supplier provides the "local" version for sending across the network or
	 * writing to disk. The predicate tests the version from a remote instance or
	 * save for acceptability (Boolean is true for network, false for local save)
	 *
	 * <p>TODO: Fabric servers do not check for client's mod list,
	 * there is no way to implement the DISPLAYTEST function in Patchwork.
	 * A Server-side Forge mod will not know if a client does not have it.
	 * Currently, this method is here purely for avoiding {@link java.lang.ClassNotFoundException}
	 */
	public static final ExtensionPoint<Pair<Supplier<String>, BiPredicate<String, Boolean>>> DISPLAYTEST = new ExtensionPoint<>();

	// Forge's unused private field
	// private Class<T> type;

	private ExtensionPoint() {
	}
}
