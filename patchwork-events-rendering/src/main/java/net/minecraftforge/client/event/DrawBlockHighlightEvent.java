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

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

/**
 * An event called whenever the selection highlight around blocks is about to be rendered.
 * Canceling this event stops the selection highlight from being rendered.
 */
//TODO: in 1.15 rename to DrawHighlightEvent
public class DrawBlockHighlightEvent extends Event {
	private final WorldRenderer context;
	private final Camera info;
	private final HitResult target;
	private final int subID;
	private final float partialTicks;

	public DrawBlockHighlightEvent(WorldRenderer context, Camera info, HitResult target, int subID, float partialTicks) {
		this.context = context;
		this.info = info;
		this.target = target;
		this.subID = subID;
		this.partialTicks = partialTicks;
	}

	public WorldRenderer getContext() {
		return context;
	}

	public Camera getInfo() {
		return info;
	}

	public HitResult getTarget() {
		return target;
	}

	public int getSubID() {
		return subID;
	}

	public float getPartialTicks() {
		return partialTicks;
	}

	@Override
	public boolean isCancelable() {
		return true;
	}

	/**
	 * A variant of the DrawHighlightEvent only called when a block is highlighted.
	 */
	public static class HighlightBlock extends DrawBlockHighlightEvent {
		public HighlightBlock(WorldRenderer context, Camera info, HitResult target, int subID, float partialTicks) {
			super(context, info, target, subID, partialTicks);
		}

		@Override
		public BlockHitResult getTarget() {
			return (BlockHitResult) super.target;
		}
	}

	/**
	 * A variant of the DrawHighlightEvent only called when an entity is highlighted.
	 * Canceling this event has no effect.
	 */
	public static class HighlightEntity extends DrawBlockHighlightEvent {
		public HighlightEntity(WorldRenderer context, Camera info, HitResult target, int subID, float partialTicks) {
			super(context, info, target, subID, partialTicks);
		}

		@Override
		public EntityHitResult getTarget() {
			return (EntityHitResult) super.target;
		}
	}
}
