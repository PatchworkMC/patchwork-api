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

import java.util.Collection;

import net.minecraftforge.common.MinecraftForge;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

/**
 * LivingDropsEvent is fired when an Entity's death causes dropped items to appear.
 *
 * <p>This event is fired whenever an Entity dies and drops items in {@link LivingEntity#onDeath(DamageSource)}.</p>
 *
 * <p>
 * {@link #source} contains the {@link DamageSource} that caused the drop to occur.<br>
 * {@link #drops} contains the Collection of {@link ItemEntity}s that will be dropped.<br>
 * {@link #lootingLevel} contains the level of Looting used to kill the entity doing the drop.<br>
 * {@link #recentlyHit} determines whether the entity doing the drop has recently been damaged by a player or tamed wolf.<br>
 * </p>
 *
 * <p>This event is cancelable. If this event is canceled, the Entity does not drop anything.</p>
 *
 * <p>This event does not have a result.</p>
 *
 * <p>This event is fired on the {@link MinecraftForge#EVENT_BUS}.</p>
 */
public class LivingDropsEvent extends LivingEvent {
	private final DamageSource source;
	private final Collection<ItemEntity> drops;
	private final int lootingLevel;
	private final boolean recentlyHit;

	public LivingDropsEvent(LivingEntity entity, DamageSource source, Collection<ItemEntity> drops, int lootingLevel, boolean recentlyHit) {
		super(entity);
		this.source = source;
		this.drops = drops;
		this.lootingLevel = lootingLevel;
		this.recentlyHit = recentlyHit;
	}

	public DamageSource getSource() {
		return source;
	}

	public Collection<ItemEntity> getDrops() {
		return drops;
	}

	public int getLootingLevel() {
		return lootingLevel;
	}

	public boolean isRecentlyHit() {
		return recentlyHit;
	}

	@Override
	public boolean isCancelable() {
		return true;
	}
}

