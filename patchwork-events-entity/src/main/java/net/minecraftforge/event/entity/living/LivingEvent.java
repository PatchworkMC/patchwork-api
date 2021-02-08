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

package net.minecraftforge.event.entity.living;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;

import net.minecraft.entity.LivingEntity;

/**
 * LivingEvent is fired whenever an event involving Living entities occurs.<br>
 * If a method utilizes this {@link net.minecraftforge.eventbus.api.Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.<br>
**/
public class LivingEvent extends EntityEvent {
	private final LivingEntity entityLiving;

	public LivingEvent(LivingEntity entity) {
		super(entity);
		entityLiving = entity;
	}

	public LivingEntity getEntityLiving() {
		return entityLiving;
	}

	/**
	 * LivingUpdateEvent is fired when an Entity is updated.
	 *
	 * <p>This event is fired whenever an Entity is updated in
	 * {@link LivingEntity#tick()}.</p>
	 *
	 * <p>This event is cancellable.
	 * If this event is canceled, the Entity does not update.</p>
	 *
	 * <p>This event does not have a result.</p>
	 *
	 * <p>This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.</p>
	 */
	public static class LivingUpdateEvent extends LivingEvent {
		public LivingUpdateEvent(LivingEntity e) {
			super(e);
		}

		@Override
		public boolean isCancelable() {
			return true;
		}
	}

	/**
	 * LivingJumpEvent is fired when an Entity jumps.
	 *
	 * <p>This event is fired whenever an Entity jumps in
	 * {@link LivingEntity#jump()} and {@link net.minecraft.entity.mob.MagmaCubeEntity#jump()}.</p>
	 *
	 * <p>This event is fired via the {@link net.minecraftforge.common.ForgeHooks#onLivingJump(LivingEntity)}.</p>
	 *
	 * <p>This event is not cancellable.</p>
	 *
	 * <p>This event does not have a result.</p>
	 *
	 * <p>This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.</p>
	 */
	public static class LivingJumpEvent extends LivingEvent {
		public LivingJumpEvent(LivingEntity e) {
			super(e);
		}
	}
}
