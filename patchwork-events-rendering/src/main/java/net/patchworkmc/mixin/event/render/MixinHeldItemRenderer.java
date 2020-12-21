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

package net.patchworkmc.mixin.event.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import net.patchworkmc.impl.event.render.RenderEvents;

@Mixin(HeldItemRenderer.class)
public abstract class MixinHeldItemRenderer {
	@Shadow
	public abstract void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float f, ItemStack item, float equipProgress);

	@Redirect(
			method = "renderFirstPersonItem(F)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;F)V",
					ordinal = 0
			)
	)
	private void redirect_renderFirstPersonItemMainHand(HeldItemRenderer heldItemRenderer, AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack itemStack, float equipProgress) {
		if (!RenderEvents.onRenderSpecificHand(Hand.MAIN_HAND, tickDelta, pitch, swingProgress, equipProgress, itemStack)) {
			this.renderFirstPersonItem(player, tickDelta, pitch, hand, swingProgress, itemStack, equipProgress);
		}
	}

	@Redirect(
			method = "renderFirstPersonItem(F)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;F)V",
					ordinal = 1
			)
	)
	private void redirect_renderFirstPersonItemOffHand(HeldItemRenderer heldItemRenderer, AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack itemStack, float equipProgress) {
		if (!RenderEvents.onRenderSpecificHand(Hand.OFF_HAND, tickDelta, pitch, swingProgress, equipProgress, itemStack)) {
			this.renderFirstPersonItem(player, tickDelta, pitch, hand, swingProgress, itemStack, equipProgress);
		}
	}
}
