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

import net.minecraft.client.util.Window;

public class RenderGameOverlayEvent extends Event {
	private final float partialTicks;
	private final Window window;
	private final ElementType type;

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
		HELMET,
		PORTAL,
		CROSSHAIRS,
		BOSSHEALTH,  // All boss bars
		BOSSINFO,    // Individual boss bar
		ARMOR,
		HEALTH,
		FOOD,
		AIR,
		HOTBAR,
		EXPERIENCE,
		TEXT,
		HEALTHMOUNT,
		JUMPBAR,
		CHAT,
		PLAYER_LIST,
		DEBUG,
		POTION_ICONS,
		SUBTITLES,
		FPS_GRAPH,
		VIGNETTE
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

	public static class Pre extends RenderGameOverlayEvent {
		public Pre(RenderGameOverlayEvent parent, ElementType type) {
			super(parent, type);
		}
	}

	public static class Post extends RenderGameOverlayEvent {
		public Post(RenderGameOverlayEvent parent, ElementType type) {
			super(parent, type);
		}

		@Override public boolean isCancelable() {
			return false;
		}
	}

	// TODO: BossInfo, Text, Chat

	@Override
	public boolean isCancelable() {
		return true;
	}
}
