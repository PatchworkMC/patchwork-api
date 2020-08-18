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

package net.minecraftforge.common.util;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;

import net.minecraft.server.world.ServerWorld;

public class FakePlayerFactory {
	private static final GameProfile MINECRAFT = new GameProfile(UUID.fromString("41C82C87-7AfB-4024-BA57-13D2C99CAE77"), "[Minecraft]");
	// Map of all active fake player profiles to their entities
	private static final Map<GameProfile, FakePlayer> fakePlayers = Maps.newHashMap();
	private static WeakReference<FakePlayer> MINECRAFT_PLAYER = null;

	public static FakePlayer getMinecraft(ServerWorld world) {
		FakePlayer ret = MINECRAFT_PLAYER != null ? MINECRAFT_PLAYER.get() : null;

		if (ret == null) {
			ret = FakePlayerFactory.get(world, MINECRAFT);
			MINECRAFT_PLAYER = new WeakReference<>(ret);
		}

		return ret;
	}

	/**
	 * Get a fake player with a given profile.
	 * Mods should either hold weak references to the return value, or listen for a
	 * WorldEvent.Unload and kill all references to prevent worlds staying in memory.
	 */
	public static FakePlayer get(ServerWorld world, GameProfile profile) {
		return fakePlayers.computeIfAbsent(profile, it -> new FakePlayer(world, it));
	}

	/**
	 * Used internally to clean up fake players when worlds are unloaded on server stop.
	 */
	public static void unloadWorld(ServerWorld world) {
		fakePlayers.entrySet().removeIf(entry -> entry.getValue().world == world);

		// This shouldn't be strictly necessary, but let's be aggressive.
		FakePlayer mc = MINECRAFT_PLAYER != null ? MINECRAFT_PLAYER.get() : null;

		if (mc != null && mc.world == world) {
			MINECRAFT_PLAYER = null;
		}
	}
}
