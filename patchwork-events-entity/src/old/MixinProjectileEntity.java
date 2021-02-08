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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.hit.HitResult;

import net.patchworkmc.impl.event.entity.EntityEventsOld;

@Mixin(PersistentProjectileEntity.class)
public class MixinProjectileEntity {
	/**
	 * Mixin to the projectile hit method, to call {@link net.minecraftforge.event.entity.ProjectileImpactEvent}.
	 *
	 * <p>This will cancel the rest of onHit if the event pipeline requests it, but a Fabric mod with higher
	 * priority can still inject into its head.</p>
	 *
	 * <p>This mixin is implemented differently from the other onProjectileImpact mixins because there is no check that
	 * the hit didn't miss before calling onHit, so we need to maintain calls to onHit for missed shots, but still need
	 * to check that it didn't miss before calling onProjectileImpact.</p>
	 */
	@Inject(method = "onHit(Lnet/minecraft/util/hit/HitResult;)V", at = @At("HEAD"), cancellable = true)
	private void hookHit(HitResult hitResult, CallbackInfo callback) {
		if (hitResult.getType() != HitResult.Type.MISS) {
			PersistentProjectileEntity entity = (PersistentProjectileEntity) (Object) this;

			if (EntityEventsOld.onProjectileImpact(entity, hitResult)) {
				callback.cancel();
			}
		}
	}
}
