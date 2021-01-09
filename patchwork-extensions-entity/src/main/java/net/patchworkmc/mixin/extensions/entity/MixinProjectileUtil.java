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

package net.patchworkmc.mixin.extensions.entity;

import net.minecraftforge.common.extensions.IForgeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;

@Mixin(ProjectileUtil.class)
public class MixinProjectileUtil {
	/**
	 * Targets an if condition that determines if a shooter is riding a target, not allowing interactions if so.
	 *
	 * <p>As an added condition of this check, we see if the target allows {@link IForgeEntity#canRiderInteract()}.
	 * If it does, we intentionally cause the if condition to fail by making {@code rider.getRootVehicle()} return null,
	 * allowing the two entities to interact.</p>
	 *
	 * @param targetEntity The entity targeted in the raycast
	 * @return The root entity of the target, or null if the rider is allowed to interact with the target.
	 */
	@Redirect(method = "raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getRootVehicle()Lnet/minecraft/entity/Entity;", ordinal = 0))
	private static Entity onRiderCheck(Entity targetEntity) {
		if (((IForgeEntity) targetEntity).canRiderInteract()) {
			return null;
		} else {
			return targetEntity.getRootVehicle();
		}
	}
}
