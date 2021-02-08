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

import net.minecraftforge.event.entity.player.PlayerEvent;

import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;

public abstract class RenderPlayerEvent extends PlayerEvent {
	private final PlayerEntityRenderer renderer;
	private final float partialRenderTick;
	private final double x;
	private final double y;
	private final double z;

	public RenderPlayerEvent(PlayerEntity player, PlayerEntityRenderer renderer, float partialRenderTick, double x, double y, double z) {
		super(player);
		this.renderer = renderer;
		this.partialRenderTick = partialRenderTick;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public PlayerEntityRenderer getRenderer() {
		return renderer;
	}

	public float getPartialRenderTick() {
		return partialRenderTick;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public static class Pre extends RenderPlayerEvent {
		public Pre(PlayerEntity player, PlayerEntityRenderer renderer, float tick, double x, double y, double z) {
			super(player, renderer, tick, x, y, z);
		}

		@Override
		public boolean isCancelable() {
			return true;
		}
	}

	public static class Post extends RenderPlayerEvent {
		public Post(PlayerEntity player, PlayerEntityRenderer renderer, float tick, double x, double y, double z) {
			super(player, renderer, tick, x, y, z);
		}
	}
}
