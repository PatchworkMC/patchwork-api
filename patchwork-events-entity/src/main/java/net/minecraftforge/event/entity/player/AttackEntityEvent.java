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

package net.minecraftforge.event.entity.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

/**
 * AttackEntityEvent is fired when a player attacks an Entity.
 *
 * <p>This event is fired whenever a player attacks an Entity in
 * {@link PlayerEntity#attack(Entity)} on either the client or the server.
 * This is in contrast to {@link net.fabricmc.fabric.api.event.player.AttackEntityCallback}, which is only called on the
 * logical server.
 *
 * <p>This event is cancellable.
 * If this event is canceled, the player does not attack the Entity.
 *
 * <p>This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
 */
public class AttackEntityEvent extends PlayerEvent {
	private final Entity target;

	public AttackEntityEvent(PlayerEntity player, Entity target) {
		super(player);

		this.target = target;
	}

	/**
	 * @return the {@link Entity} that was damaged by the player.
	 */
	public Entity getTarget() {
		return target;
	}

	@Override
	public boolean isCancelable() {
		return true;
	}
}
