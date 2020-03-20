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

package net.minecraftforge.client.event;

import net.minecraftforge.eventbus.api.Event;

import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.util.Window;

public class RenderGameOverlayEvent extends Event {
	public float getPartialTicks() {
		return partialTicks;
	}

	public Window getWindow() {
		return window;
	}

	public ElementType getType() {
		return type;
	}

	public enum ElementType {
		ALL,
		// HELMET,
		// PORTAL,
		// CROSSHAIRS,
		// BOSSHEALTH, // All boss bars
		// BOSSINFO,    // Individual boss bar
		ARMOR,
		HEALTH,
		FOOD,
		AIR,
		// HOTBAR,
		// EXPERIENCE,
		// TEXT,
		HEALTHMOUNT,
		// JUMPBAR,
		// CHAT,
		// PLAYER_LIST,
		// DEBUG,
		// POTION_ICONS,
		// SUBTITLES,
		// FPS_GRAPH,
		// VIGNETTE
	}

	private final float partialTicks;
	private final Window window;
	private final ElementType type;

	// For EventBus
	public RenderGameOverlayEvent() {
		this.partialTicks = -1;
		this.window = null;
		this.type = null;
	}

	public RenderGameOverlayEvent(float partialTicks, Window window) {
		this.partialTicks = partialTicks;
		this.window = window;
		this.type = null;
	}

	private RenderGameOverlayEvent(RenderGameOverlayEvent parent, ElementType type) {
		this.partialTicks = parent.getPartialTicks();
		this.window = parent.getWindow();
		this.type = type;
	}

	@Override
	public boolean isCancelable() {
		return true;
	}

	public static class Pre extends RenderGameOverlayEvent {
		// For EventBus
		public Pre() {
		}

		public Pre(RenderGameOverlayEvent parent, ElementType type) {
			super(parent, type);
		}
	}

	/**
	 * This event is not cancellable.
	 */
	public static class Post extends RenderGameOverlayEvent {
		// For EventBus
		public Post() {
		}

		public Post(RenderGameOverlayEvent parent, ElementType type) {
			super(parent, type);
		}

		@Override
		public boolean isCancelable() {
			return false;
		}
	}

	public static class BossInfo extends Pre {
		private final ClientBossBar bossBar;
		private final int x;
		private final int y;
		private int increment;

		// For EventBus
		public BossInfo() {
			this.bossBar = null;
			this.x = -1;
			this.y = -1;
		}

		public BossInfo(RenderGameOverlayEvent parent, ElementType type, ClientBossBar bossBar, int x, int y, int increment) {
			super(parent, type);
			this.bossBar = bossBar;
			this.x = x;
			this.y = y;
			this.increment = increment;
		}

		/**
		 * @return The {@link ClientBossBar} currently being rendered
		 */
		public ClientBossBar getBossInfo() {
			return bossBar;
		}

		/**
		 * @return The current x position we are rendering at
		 */
		public int getX() {
			return x;
		}

		/**
		 * @return The current y position we are rendering at
		 */
		public int getY() {
			return y;
		}

		/**
		 * @return How much to move down before rendering the next bar
		 */
		public int getIncrement() {
			return increment;
		}

		/**
		 * Sets the amount to move down before rendering the next bar.
		 * @param increment The increment to set
		 */
		public void setIncrement(int increment) {
			this.increment = increment;
		}
	}
}
