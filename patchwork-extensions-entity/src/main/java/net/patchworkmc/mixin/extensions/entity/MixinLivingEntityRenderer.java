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

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer {
	/**
	 * Handles the {@link IForgeEntity#shouldRiderSit()} hook for determining if riders should be rendered as sitting.
	 *
	 * <p>This method intentionally targets every invocation of {@link LivingEntity#hasVehicle()}.</p>
	 *
	 * @param entity The "rider" entity being rendered
	 * @return If the entity should be rendered as sitting
	 */
	@Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasVehicle()Z"))
	private boolean redirectHasVehicle(LivingEntity entity) {
		return entity.hasVehicle() && ((IForgeEntity) entity.getVehicle()).shouldRiderSit();
	}
}
