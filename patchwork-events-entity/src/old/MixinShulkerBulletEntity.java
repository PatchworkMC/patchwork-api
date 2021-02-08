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

import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.hit.HitResult;

import net.patchworkmc.impl.event.entity.EntityEventsOld;

@Mixin(ShulkerBulletEntity.class)
public abstract class MixinShulkerBulletEntity {
	@Shadow
	protected abstract void onHit(HitResult hitResult);

	/**
	 * Mixin to the redirect the ShulkerBulletEntity hit method, to call {@link net.minecraftforge.event.entity.ProjectileImpactEvent}.
	 *
	 * <p>Note: In future versions, the @Redirect should be replaced with a @ModifyVariable to the bl flag.</p>
	 */
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ShulkerBulletEntity;onHit(Lnet/minecraft/util/hit/HitResult;)V"))
	private void hookTick(ShulkerBulletEntity entity, HitResult hitResult) {
		if (!EntityEventsOld.onProjectileImpact(entity, hitResult)) {
			this.onHit(hitResult);
		}
	}
}
