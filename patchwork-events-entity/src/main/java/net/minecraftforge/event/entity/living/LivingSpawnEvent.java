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

import javax.annotation.Nullable;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.WorldAccess;

/**
 * <p>LivingSpawnEvent is fired for any events associated with Living Entities spawn status.
 * If a method utilizes this {@link net.minecraftforge.eventbus.api.event.Event} as its parameter, the method will
 * receive every child event of this class.</p>
 *
 * <p>{@link #world} contains the world in which this living Entity is being spawned.
 * {@link #x} contains the x-coordinate this entity is being spawned at.
 * {@link #y} contains the y-coordinate this entity is being spawned at.
 * {@link #z} contains the z-coordinate this entity is being spawned at.</p>
 *
 * <p>All children of this event are fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.</p>
 */
public class LivingSpawnEvent extends LivingEvent {
	private final WorldAccess world;
	private final double x;
	private final double y;
	private final double z;

	public LivingSpawnEvent(MobEntity entity, WorldAccess world, double x, double y, double z) {
		super(entity);
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public WorldAccess getWorld() {
		return world;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	@Override
	public boolean hasResult() {
		return true;
	}

	/**
	 * Fires before mob spawn events.
	 *
	 * <p>Result is significant:<br>
	 * DEFAULT: use vanilla spawn rules<br>
	 * ALLOW: allow the spawn<br>
	 * DENY: deny the spawn</p>
	 *
	 */
	public static class CheckSpawn extends LivingSpawnEvent {
		@Nullable
		private final MobSpawnerLogic spawner;
		private final SpawnReason type;

		/**
		 * CheckSpawn is fired when an Entity is about to be spawned.
		 *
		 * @param entity the spawning entity
		 * @param world the world to spawn in
		 * @param x x coordinate
		 * @param y y coordinate
		 * @param z z coordinate
		 * @param spawner the spawner that spawned this entity null if it this spawn is
		 *               coming from a world spawn or other non-spawner source
		 */
		public CheckSpawn(MobEntity entity, WorldAccess world, double x, double y, double z,
				@Nullable MobSpawnerLogic spawner, SpawnReason type) {
			super(entity, world, x, y, z);
			this.spawner = spawner;
			this.type = type;
		}

		public boolean isSpawner() {
			return spawner != null;
		}

		@Nullable
		public MobSpawnerLogic getSpawner() {
			return spawner;
		}

		public SpawnReason getSpawnReason() {
			return type;
		}

		@Override
		public boolean hasResult() {
			return true;
		}
	}

	/**
	 * <p>SpecialSpawn is fired when an Entity is to be spawned.
	 * This allows you to do special initializers in the new entity.</p>
	 *
	 * <p>This event is fired via {@link net.patchworkmc.impl.event.entity.EntityEvents#doSpecialSpawn(MobEntity, WorldAccess, double, double, double, MobSpawnerLogic, SpawnReason)}.</p>
	 *
	 * <p>This event is cancellable.
	 * If this event is canceled, the entity's {@link MobEntity#initialize} method is not called.</p>
	 *
	 * <p>This event does not have a result.</p>
	 *
	 * <p>This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.</p>
	 */
	public static class SpecialSpawn extends LivingSpawnEvent {
		@Nullable
		private final MobSpawnerLogic spawner;
		private final SpawnReason type;

		/**
		 * @param entity the spawning entity
		 * @param world the world to spawn in
		 * @param x x coordinate
		 * @param y y coordinate
		 * @param z z coordinate
		 * @param spawner the spawner that spawned this entity, or null if this spawn is
		 *        coming from a world spawn or other non-spawner source
		 */
		public SpecialSpawn(MobEntity entity, WorldAccess world, double x, double y, double z,
				@Nullable MobSpawnerLogic spawner, SpawnReason type) {
			super(entity, world, x, y, z);
			this.spawner = spawner;
			this.type = type;
		}

		public boolean isSpawner() {
			return spawner != null;
		}

		@Nullable
		public MobSpawnerLogic getSpawner() {
			return spawner;
		}

		public SpawnReason getSpawnReason() {
			return type;
		}

		@Override
		public boolean isCancelable() {
			return true;
		}
	}
}
