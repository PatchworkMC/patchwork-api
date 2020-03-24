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

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

/**
 * LivingDeathEvent is fired when a {@link LivingEntity} dies.
 *
 * <p>This event is fired whenever a {@link LivingEntity} dies in
 * {@link LivingEntity#onDeath(DamageSource)},
 * {@link net.minecraft.entity.player.PlayerEntity#onDeath(DamageSource)}, and
 * {@link net.minecraft.server.network.ServerPlayerEntity#onDeath(DamageSource)}.</p>
 *
 * <p>This event is fired via {@link net.patchworkmc.impl.event.entity.EntityEvents#onLivingDeath(LivingEntity, DamageSource)}.</p>
 *
 * <p>{@link #source} contains the {@link DamageSource} that caused the {@link LivingEntity} to die.</p>
 *
 * <p>This event is cancellable.
 * If this event is canceled, the {@link LivingEntity} does not die.</p>
 *
 * <p>This event is fired on the {@link MinecraftForge#EVENT_BUS}.</p>
 */
public class LivingDeathEvent extends LivingEvent {
	private final DamageSource source;

	// For EventBus
	public LivingDeathEvent() {
		super();

		this.source = null;
	}

	public LivingDeathEvent(LivingEntity entity, DamageSource source) {
		super(entity);
		this.source = source;
	}

	public DamageSource getSource() {
		return source;
	}

	@Override
	public boolean isCancelable() {
		return true;
	}
}
