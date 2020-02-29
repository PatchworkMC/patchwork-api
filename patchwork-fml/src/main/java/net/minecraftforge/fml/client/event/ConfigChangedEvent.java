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

package net.minecraftforge.fml.client.event;

import javax.annotation.Nullable;

import net.minecraftforge.eventbus.api.Event;

/**
 * These events are posted from the config screen when the done button is pressed.
 *
 * <p>Listeners for this event should use {@link OnConfigChangedEvent} or {@link PostConfigChangedEvent} and check for a specific mod ID.
 * For best results the listener should refresh any objects/fields that are set based on the mod's config
 * and should serialize the modified config.</p>
 *
 * <p>TODO: These events will never be fired by Patchwork (until a ModMenu integration?)</p>
 * @author bspkrs
 */
public class ConfigChangedEvent extends Event {
	private final String modID;
	private final boolean isWorldRunning;
	private final boolean requiresMcRestart;
	@Nullable
	private final String configID;

	// For EventBus
	public ConfigChangedEvent() {
		this(null, null, false, false);
	}

	public ConfigChangedEvent(String modID, @Nullable String configID, boolean isWorldRunning, boolean requiresMcRestart) {
		this.modID = modID;
		this.configID = configID;
		this.isWorldRunning = isWorldRunning;
		this.requiresMcRestart = requiresMcRestart;
	}

	@Override
	public boolean hasResult() {
		return true;
	}

	/**
	 * The Mod ID of the mod whose configuration just changed.
	 */
	public String getModID() {
		return modID;
	}

	/**
	 * Whether or not a world is currently running.
	 * TODO: does this mean a physical server running on the client, or if the client is currently in *any* world?
	 */
	public boolean isWorldRunning() {
		return isWorldRunning;
	}

	/**
	 * Will be set to true if any elements were changed that require a restart of Minecraft.
	 */
	public boolean isRequiresMcRestart() {
		return requiresMcRestart;
	}

	/**
	 * A String identifier referring to the specific config that was changed.
	 */
	@Nullable
	public String getConfigID() {
		return configID;
	}

	/**
	 * This event is intended to be consumed by the mod whose config has been changed. It fires when the Done button
	 * has been clicked on the config screen and at least one element has been changed.
	 * Modders should check the modID field of the event to ensure they are only acting on their own config screen's event!
	 */
	public static class OnConfigChangedEvent extends ConfigChangedEvent {
		// For EventBus
		public OnConfigChangedEvent() {
		}

		public OnConfigChangedEvent(String modID, @Nullable String configID, boolean isWorldRunning, boolean requiresMcRestart) {
			super(modID, configID, isWorldRunning, requiresMcRestart);
		}
	}

	/**
	 * This event is provided for mods to consume if they want to be able to check if other mods' configs have been changed.
	 * This event only fires if the OnConfigChangedEvent result is not DENY.
	 */
	public static class PostConfigChangedEvent extends ConfigChangedEvent {
		// For EventBus
		public PostConfigChangedEvent() {
		}

		public PostConfigChangedEvent(String modID, @Nullable String configID, boolean isWorldRunning, boolean requiresMcRestart) {
			super(modID, configID, isWorldRunning, requiresMcRestart);
		}
	}
}
