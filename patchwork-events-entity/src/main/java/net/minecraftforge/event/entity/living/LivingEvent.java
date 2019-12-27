/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2019, 2019
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

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * LivingEvent is fired whenever an event involving Living entities occurs.<br>
 * If a method utilizes this {@link net.minecraftforge.eventbus.api.Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
public class LivingEvent extends EntityEvent {
	private final LivingEntity livingEntity;

	// For EventBus
	public LivingEvent() {
		this(null);
	}

	public LivingEvent(LivingEntity entity) {
		super(entity);

		livingEntity = entity;
	}

	public LivingEntity getEntityLiving() {
		return livingEntity;
	}

	/**
	 * LivingUpdateEvent is fired when an Entity is updated. <br>
	 * This event is fired whenever an Entity is updated in
	 * {@link EntityLivingBase#onUpdate()}. <br>
	 * <br>
	 * This event is fired via the {@link net.minecraftforge.common.ForgeHooks#onLivingUpdate(EntityLivingBase)}.<br>
	 * <br>
	 * This event is {@link Cancelable}.<br>
	 * If this event is canceled, the Entity does not update.<br>
	 * <br>
	 * This event does not have a result. {@link HasResult}<br>
	 * <br>
	 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
	 **/
	/* TODO @Cancelable
	public static class LivingUpdateEvent extends LivingEvent
	{
		public LivingUpdateEvent(LivingEntity e){ super(e); }
	}*/

	/**
	 * LivingJumpEvent is fired when an Entity jumps.<br>
	 * This event is fired whenever an Entity jumps in
	 * {@link EntityLivingBase#jump()}, {@link EntityMagmaCube#jump()},
	 * and {@link EntityHorse#jump()}.<br>
	 * <br>
	 * This event is fired via the {@link net.minecraftforge.common.ForgeHooks#onLivingJump(EntityLivingBase)}.<br>
	 * <br>
	 * This event is not {@link Cancelable}.<br>
	 * <br>
	 * This event does not have a result. {@link HasResult}<br>
	 * <br>
	 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
	 **/
	/* TODO public static class LivingJumpEvent extends LivingEvent
	{
		public LivingJumpEvent(LivingEntity e){ super(e); }
	}*/
}
