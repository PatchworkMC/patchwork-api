/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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
import net.minecraft.entity.damage.DamageSource;
import net.minecraftforge.common.MinecraftForge;

/**
 * LivingDeathEvent is fired when an Entity dies.
 * <p>This event is fired whenever an Entity dies in
 * {@link LivingEntity#onDeath(DamageSource)},
 * {@link net.minecraft.entity.player.PlayerEntity#onDeath(DamageSource)}, and
 * {@link net.minecraft.server.network.ServerPlayerEntity#onDeath(DamageSource)}.</p>
 *
 * <p>This event is fired via {@link com.patchworkmc.impl.event.entity.EntityEvents#onLivingDeath(LivingEntity, DamageSource)}.</p>
 *
 * <p>{@link #source} contains the DamageSource that caused the entity to die.</p>
 *
 * <p>This event is cancellable.
 * If this event is canceled, the Entity does not die.</p>
 *
 * <p>This event is fired on the {@link MinecraftForge#EVENT_BUS}.</p>
 **/
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
