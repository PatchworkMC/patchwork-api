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

package net.minecraftforge.event.entity;

import net.minecraftforge.common.MinecraftForge;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

/**
 * This event is fired when an {@link Entity} joins the world.
 *
 * <p>This event is fired whenever an {@link Entity} is added to the world in:
 * <ul>
 * <li>{@link net.minecraft.client.world.ClientWorld#addEntityPrivate(int, Entity)}</li>
 * <li>{@link net.minecraft.server.world.ServerWorld#addPlayer(ServerPlayerEntity)}</li>
 * <li>{@link net.minecraft.server.world.ServerWorld#addEntity(Entity)}</li>
 * <li>{@link net.minecraft.server.world.ServerWorld#loadEntity(Entity)}</li>
 * </ul>
 * </p>
 *
 * <p>{@link #world} contains the world in which the entity is to join.</p>
 *
 * <p>This event is cancellable.
 * If this event is canceled, the Entity is not added to the world.</p>
 *
 * <p>This event is fired on the {@link MinecraftForge#EVENT_BUS}.</p>
 */
public class EntityJoinWorldEvent extends EntityEvent {
	private final World world;

	// For EventBus
	public EntityJoinWorldEvent() {
		super();

		this.world = null;
	}

	public EntityJoinWorldEvent(Entity entity, World world) {
		super(entity);
		this.world = world;
	}

	public World getWorld() {
		return world;
	}

	@Override
	public boolean isCancelable() {
		return true;
	}
}
