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

import net.minecraft.entity.ItemEntity;

/**
 * Event that is fired when an ItemEntity's age has reached its maximum
 * lifespan. If cancelled, the ItemEntity will not be removed from the world,
 * add extraLife time will be added to the item's life.
 */
public class ItemExpireEvent extends ItemEvent {
	private int extraLife;

	/**
	 * Creates a new event for an expiring ItemEntity.
	 *
	 * @param itemEntity The ItemEntity being deleted.
	 * @param extraLife The amount of time to be added to this entities lifespan if the event is canceled.
	 */
	public ItemExpireEvent(ItemEntity itemEntity, int extraLife) {
		super(itemEntity);
		this.setExtraLife(extraLife);
	}

	public int getExtraLife() {
		return this.extraLife;
	}

	public void setExtraLife(int extraLife) {
		this.extraLife = extraLife;
	}

	@Override
	public boolean isCancelable() {
		return true;
	}
}
