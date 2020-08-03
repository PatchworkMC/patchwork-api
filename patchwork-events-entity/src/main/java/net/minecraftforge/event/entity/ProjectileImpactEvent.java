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

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.thrown.ThrownEntity;
import net.minecraft.util.hit.HitResult;

/**
 * This event is fired when a projectile entity impacts something.
 *
 * <p>Subclasses of this event exist for more specific types of projectile.</p>
 *
 * <p>This event is fired for all vanilla projectiles by Patchwork.
 * Custom projectiles should fire this event via {@link net.patchworkmc.impl.event.entity.EntityEvents}, check the result,
 * and cancel the impact if false.</p>
 *
 * <p>This event is cancelable. When canceled, the impact will not be processed.
 * Killing or other handling of the entity after event cancellation is up to the modder.</p>
 *
 * <p>This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.</p>
 */
public class ProjectileImpactEvent extends EntityEvent {
	private final HitResult ray;

	public ProjectileImpactEvent(Entity entity, HitResult ray) {
		super(entity);
		this.ray = ray;
	}

	@Override
	public boolean isCancelable() {
		return true;
	}

	public HitResult getRayTraceResult() {
		return ray;
	}

	public static class Arrow extends ProjectileImpactEvent {
		private final ProjectileEntity arrow;

		public Arrow(ProjectileEntity arrow, HitResult ray) {
			super(arrow, ray);
			this.arrow = arrow;
		}

		public ProjectileEntity getArrow() {
			return arrow;
		}
	}

	public static class Fireball extends ProjectileImpactEvent {
		private final ExplosiveProjectileEntity fireball;

		public Fireball(ExplosiveProjectileEntity fireball, HitResult ray) {
			super(fireball, ray);
			this.fireball = fireball;
		}

		public ExplosiveProjectileEntity getFireball() {
			return fireball;
		}
	}

	public static class Throwable extends ProjectileImpactEvent {
		private final ThrownEntity throwable;

		public Throwable(ThrownEntity throwable, HitResult ray) {
			super(throwable, ray);
			this.throwable = throwable;
		}

		public ThrownEntity getThrowable() {
			return throwable;
		}
	}
}
