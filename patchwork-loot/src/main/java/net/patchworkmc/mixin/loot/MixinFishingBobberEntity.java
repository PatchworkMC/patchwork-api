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

package net.patchworkmc.mixin.loot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;

@Mixin(FishingBobberEntity.class)
public class MixinFishingBobberEntity {
	private static final String LOOT_CONTEXT_BUILD_TARGET =
			"net/minecraft/loot/context/LootContext$Builder.build(Lnet/minecraft/loot/context/LootContextType;)Lnet/minecraft/loot/context/LootContext;";

	@Shadow
	private final PlayerEntity owner = null;

	@Inject(method = "method_6957(Lnet/minecraft/item/ItemStack;)I", at = @At(value = "INVOKE", target = LOOT_CONTEXT_BUILD_TARGET), locals = LocalCapture.CAPTURE_FAILHARD)
	private void patchwork_addFishingParameters(ItemStack stack, CallbackInfoReturnable<Integer> callback, int rodDamage, LootContext.Builder builder, LootTable supplier) {
		builder.parameter(LootContextParameters.KILLER_ENTITY, this.owner);
		builder.parameter(LootContextParameters.THIS_ENTITY, (Entity) (Object) this);
	}
}
