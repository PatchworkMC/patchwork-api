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

package net.patchworkmc.mixin.event.entity.old;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.util.hit.HitResult;

import net.patchworkmc.impl.event.entity.EntityEventsOld;

@Mixin(ExplosiveProjectileEntity.class)
public abstract class MixinExplosiveProjectileEntity {
	@Shadow
	protected abstract void onCollision(HitResult hitResult);

	/**
	 * Mixin to the redirect the onCollision method, to call {@link net.minecraftforge.event.entity.ProjectileImpactEvent}.
	 *
	 * <p>Note: In future versions, the @Redirect should be replaced with a @ModifyVariable to the bl flag.</p>
	 */
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ExplosiveProjectileEntity;onCollision(Lnet/minecraft/util/hit/HitResult;)V"))
	private void hookTick(ExplosiveProjectileEntity entity, HitResult hitResult) {
		if (!EntityEventsOld.onProjectileImpact(entity, hitResult)) {
			this.onCollision(hitResult);
		}
	}
}
