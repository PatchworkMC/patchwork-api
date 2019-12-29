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
import net.minecraft.entity.damage.DamageSource;

/**
 * LivingAttackEvent is fired when a living Entity is attacked.
 *
 * <p>This event is fired whenever a LivingEntity is attacked in
 * {@link LivingEntity#damage(DamageSource, float)} and
 * {@link net.minecraft.entity.player.PlayerEntity#damage(DamageSource, float)}.</p>
 *
 * <p>This event is fired via the {@link com.patchworkmc.impl.event.entity.EntityEvents#onLivingAttack(EntityLivingBase, DamageSource, float)}.</p>
 *
 * <p>{@link #source} contains the DamageSource of the attack.
 * {@link #amount} contains the amount of damage dealt to the entity.</p>
 *
 * <p>This event is cancellable.
 * If this event is canceled, the {@link LivingEntity} does not take attack damage.</p>
 *
 * <p>This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.</p>
 */
public class LivingAttackEvent extends LivingEvent {
	private final DamageSource source;
	private final float damage;

	public LivingAttackEvent() {
		this.source = null;
		this.damage = 0f;
	}

	public LivingAttackEvent(LivingEntity entity, DamageSource source, float damage) {
		super(entity);
		this.source = source;
		this.damage = damage;
	}

	public DamageSource getSource() {
		return source;
	}

	public float getAmount() {
		return damage;
	}

	@Override
	public boolean isCancelable() {
		return true;
	}
}
