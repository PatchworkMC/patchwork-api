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

/**
 * LivingFallEvent is fired when an Entity is set to be falling.
 *
 * <p>This event is fired whenever an Entity is set to fall in
 * {@link LivingEvent#fall(float, float)}.</p>
 *
 * <p>For players that are able to fly, {@link net.minecraftforge.event.entity.player.PlayerFlyableFallEvent} will be fired instead.</p>
 *
 * <p>This event is fired via {@link com.patchworkmc.impl.event.entity.EntityEvents#onLivingFall(LivingEntity, float, float)}.</p>
 *
 * <p>{@link #distance} contains the distance the Entity is to fall. If this event is cancelled, this value is set to 0.0F.</p>
 *
 * <p>This event is cancellable.
 * If this event is cancelled, the Entity does not fall.</p>
 *
 * <p>This event is fired on the {@link MinecraftForge#EVENT_BUS}.</p>
 */
public class LivingFallEvent extends LivingEvent {
	private float distance;
	private float damageMultiplier;

	// For EventBus
	public LivingFallEvent() {
		this(null, 0, 0);
	}

	public LivingFallEvent(LivingEntity entity, float distance, float damageMultiplier) {
		super(entity);

		this.setDistance(distance);
		this.setDamageMultiplier(damageMultiplier);
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public float getDamageMultiplier() {
		return damageMultiplier;
	}

	public void setDamageMultiplier(float damageMultiplier) {
		this.damageMultiplier = damageMultiplier;
	}

	@Override
	public boolean isCancelable() {
		return true;
	}
}
