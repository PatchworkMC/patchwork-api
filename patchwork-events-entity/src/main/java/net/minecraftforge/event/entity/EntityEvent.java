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

import net.minecraftforge.eventbus.api.Event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;

/**
 * EntityEvent is fired when an event involving any Entity occurs.
 *
 * <p>If a method utilizes this {@link net.minecraftforge.eventbus.api.Event} as its parameter, the method will
 * receive every child event of this class.</p>
 *
 * <p>{@link #entity} contains the entity that caused this event to occur.</p>
 *
 * <p>All children of this event are fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.</p>
 */
public class EntityEvent extends Event {
	private final Entity entity;

	public EntityEvent(Entity entity) {
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}

	/**
	 * EntityConstructing is fired when an Entity is being created.
	 *
	 * <p>This event is fired within the constructor of the Entity.</p>
	 *
	 * <p>This event is not cancellable.</p>
	 *
	 * <p>This event does not have a result.</p>
	 *
	 * <p>This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.</p>
	 */
	public static class EntityConstructing extends EntityEvent {
		public EntityConstructing(Entity entity) {
			super(entity);
		}
	}

	/**
	 * <b>TODO: Forge never fires this event.</b>
	 *
	 * <p>{@link CanUpdate#canUpdate} contains the boolean value of whether this entity can update.</p>
	 *
	 * <p>If the modder decides that this Entity can be updated, they may change canUpdate to true,
	 * and the entity will then be updated.</p>
	 *
	 * <p>This event is not cancellable.</p>
	 *
	 * <p>This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.</p>
	 */
	/* TODO public static class CanUpdate extends EntityEvent {
		private boolean canUpdate = false;

		public CanUpdate(Entity entity) {
			super(entity);
		}

		public boolean getCanUpdate() {
			return canUpdate;
		}

		public void setCanUpdate(boolean canUpdate) {
			this.canUpdate = canUpdate;
		}
	}*/

	/**
	 * EnteringChunk is fired when an Entity enters a chunk.
	 *
	 * <p>This event is fired whenever vanilla Minecraft determines that an entity
	 * is entering a chunk in {@link net.minecraft.world.chunk.Chunk#addEntity(net.minecraft.entity.Entity)}</p>
	 *
	 * <p>This event is not cancellable.</p>
	 *
	 * <p>This event does not have a result.</p>
	 *
	 * <p>This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.</p>
	 */
	public static class EnteringChunk extends EntityEvent {
		private int newChunkX;
		private int newChunkZ;
		private int oldChunkX;
		private int oldChunkZ;

		public EnteringChunk(Entity entity, int newChunkX, int newChunkZ, int oldChunkX, int oldChunkZ) {
			super(entity);
			this.setNewChunkX(newChunkX);
			this.setNewChunkZ(newChunkZ);
			this.setOldChunkX(oldChunkX);
			this.setOldChunkZ(oldChunkZ);
		}

		public int getNewChunkX() {
			return newChunkX;
		}

		public void setNewChunkX(int newChunkX) {
			this.newChunkX = newChunkX;
		}

		public int getNewChunkZ() {
			return newChunkZ;
		}

		public void setNewChunkZ(int newChunkZ) {
			this.newChunkZ = newChunkZ;
		}

		public int getOldChunkX() {
			return oldChunkX;
		}

		public void setOldChunkX(int oldChunkX) {
			this.oldChunkX = oldChunkX;
		}

		public int getOldChunkZ() {
			return oldChunkZ;
		}

		public void setOldChunkZ(int oldChunkZ) {
			this.oldChunkZ = oldChunkZ;
		}
	}

	/**
	 * EyeHeight is fired when an Entity's eye height changes.
	 *
	 * <p>This event is fired whenever the {@link EntityPose} changes, and in a few other hardcoded scenarios.</p>
	 *
	 * <p>This event is not cancellable.</p>
	 *
	 * <p>This event does not have a result.</p>
	 *
	 * <p>This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.</p>
	 */
	public static class EyeHeight extends EntityEvent {
		private final EntityPose pose;
		private final EntityDimensions size;
		private final float oldHeight;
		private float newHeight;

		public EyeHeight(Entity entity, EntityPose pose, EntityDimensions size, float defaultHeight) {
			super(entity);
			this.pose = pose;
			this.size = size;
			this.oldHeight = defaultHeight;
			this.newHeight = defaultHeight;
		}

		public EntityPose getPose() {
			return pose;
		}

		public EntityDimensions getSize() {
			return size;
		}

		public float getOldHeight() {
			return oldHeight;
		}

		public float getNewHeight() {
			return newHeight;
		}

		public void setNewHeight(float newSize) {
			this.newHeight = newSize;
		}
	}
}
