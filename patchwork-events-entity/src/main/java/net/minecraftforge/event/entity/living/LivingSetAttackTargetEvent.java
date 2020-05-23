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

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;

/**
 * LivingSetAttackTargetEvent is fired when an entity sets a target to attack.
 *
 * <p>This event is fired whenever a {@link MobEntity} sets a target to attack in
 * {@link MobEntity#setTarget(LivingEntity)}.</p>
 *
 * <p>This event is fired via {@link net.patchworkmc.impl.event.entity.EntityEvents#onLivingSetAttackTarget(LivingEntity, LivingEntity)}.</p>
 *
 * <p>{@link #target} contains the newly targeted Entity.</p>
 *
 * <p>This event is not cancellable.</p>
 *
 * <p>This event does not have a result.</p>
 *
 * <p>This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.</p>
 */
public class LivingSetAttackTargetEvent extends LivingEvent {
	private final LivingEntity target;

	public LivingSetAttackTargetEvent(LivingEntity entity, LivingEntity target) {
		super(entity);

		this.target = target;
	}

	public LivingEntity getTarget() {
		return target;
	}
}
