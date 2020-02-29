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

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.LivingEntity;

/**
 * LivingDamageEvent is fired just before damage is applied to entity.
 *
 * <p>At this point armor, potion and absorption modifiers have already been applied to damage - this is FINAL value.</p>
 * <p>Also note that appropriate resources (like armor durability and absorption extra hearths) have already been consumed.</p>
 * <p>This event is fired whenever an Entity is damaged in
 * {@link LivingEntity#applyDamage(DamageSource, float)} and
 * {@link net.minecraft.entity.player.PlayerEntity#applyDamage(DamageSource, float)}.</p>
 *
 * <p>This event is fired via the {@link com.patchworkmc.impl.event.entity.EntityEvents#onLivingDamage(LivingEntity, DamageSource, float)}.</p>
 *
 * <p>{@link #source} contains the DamageSource that caused this Entity to be hurt. </p>
 * <p>{@link #amount} contains the final amount of damage that will be dealt to entity. </p>
 *
 * <p>This event is cancelable.</p>
 * <p>If this event is canceled, the Entity is not hurt. Used resources WILL NOT be restored.</p>
 *
 * <p>This event does not have a result.</p>
 * @see LivingHurtEvent
 */
public class LivingDamageEvent extends LivingEvent {
	private final DamageSource source;
	private float amount;

	public LivingDamageEvent(LivingEntity entity, DamageSource source, float amount) {
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
}
