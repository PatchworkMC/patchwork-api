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
 * Base class for all ItemEntity events. Contains a reference to the
 * ItemEntity of interest. For most ItemEntity events, there's little to no
 * additional useful data from the firing method that isn't already contained
 * within the ItemEntity instance.
 */
public class ItemEvent extends EntityEvent {
	private final ItemEntity entityItem;

	/**
	 * Creates a new event for an EntityItem.
	 *
	 * @param itemEntity The EntityItem for this event
	 */
	public ItemEvent(ItemEntity itemEntity) {
		super(itemEntity);
		this.entityItem = itemEntity;
	}

	/**
	 * The relevant EntityItem for this event, already cast for you.
	 */
	public ItemEntity getEntityItem() {
		return entityItem;
	}
}
