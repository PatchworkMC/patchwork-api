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
import net.minecraft.entity.damage.DamageSource;

/**
 * <p>LivingHurtEvent is fired when an Entity is set to be hurt.
 * This event is fired whenever an Entity is hurt in
 * {@link LivingEntity#applyDamage(DamageSource, float)} and
 * {@link net.minecraft.entity.player.PlayerEntity#applyDamage(DamageSource, float)}.</p>
 *
 * <p>This event is fired via {@link net.patchworkmc.impl.event.entity.EntityEvents#onLivingHurt(LivingEntity, DamageSource, float)}.</p>
 *
 * <p>{@link #source} contains the {@link DamageSource} that caused this {@link LivingEntity} to be hurt.
 * {@link #amount} contains the amount of damage dealt to the {@link LivingEntity} that was hurt.</p>
 *
 * <p>This event is cancellable.
 * If this event is canceled, the Entity is not hurt.</p>
 *
 * <p>This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.</p>
 */
public class LivingHurtEvent extends LivingEvent {
	private final DamageSource source;
	private float amount;

	public LivingHurtEvent(LivingEntity entity, DamageSource source, float amount) {
		super(entity);

		this.source = source;
		this.amount = amount;
	}

	public DamageSource getSource() {
		return source;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	@Override
	public boolean isCancelable() {
		return true;
	}
}
