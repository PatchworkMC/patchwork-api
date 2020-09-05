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

package net.minecraftforge.event.entity;

import net.minecraftforge.common.MinecraftForge;

import net.minecraft.entity.Entity;
import net.minecraft.world.dimension.DimensionType;

/**
 * <p>EntityTravelToDimensionEvent is fired before an Entity travels to a dimension.</p>
 *
 * <p>{@link #dimension} contains the id of the dimension the entity is traveling to.</p>
 *
 * <p>This event is cancelable.
 * If this event is canceled, the Entity does not travel to the dimension.</p>
 *
 * <p>This event does not have a result.</p>
 *
 * <p>This event is fired on the {@link MinecraftForge#EVENT_BUS}.</p>
 */
public class EntityTravelToDimensionEvent extends EntityEvent {
	private final DimensionType dimension;

	public EntityTravelToDimensionEvent(Entity entity, DimensionType dimension) {
		super(entity);
		this.dimension = dimension;
	}

	public DimensionType getDimension() {
		return dimension;
	}

	@Override
	public boolean isCancelable() {
		return true;
	}
}

