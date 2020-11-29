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

package net.minecraftforge.event.entity.item;

import net.minecraftforge.event.entity.EntityEvent;

import net.minecraft.entity.ItemEntity;

/**
 * Base class for all ItemEntity events, containing a reference to the
 * ItemEntity of interest.
 */
public class ItemEvent extends EntityEvent {
	private final ItemEntity itemEntity;

	/**
	 * Creates a new event for an ItemEntity.
	 *
	 * @param itemEntity The ItemEntity for this event
	 */
	public ItemEvent(ItemEntity itemEntity) {
		super(itemEntity);
		this.itemEntity = itemEntity;
	}

	/**
	 * The relevant ItemEntity for this event.
	 */
	public ItemEntity getEntityItem() {
		return itemEntity;
	}
}
