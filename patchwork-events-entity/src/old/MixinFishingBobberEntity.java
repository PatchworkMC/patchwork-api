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

import java.util.List;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;

@Mixin(FishingBobberEntity.class)
public abstract class MixinFishingBobberEntity {
	@Shadow
	private boolean field_7176; // is stuck to block

	@Shadow
	public abstract void remove();

	@Unique
	private final ThreadLocal<Integer> rodDamage = ThreadLocal.withInitial(() -> -1);

	@Inject(method = "method_6957", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/criterion/FishingRodHookedCriterion;trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/projectile/FishingBobberEntity;Ljava/util/Collection;)V", ordinal = 1), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void onReeledIn(ItemStack itemStack, CallbackInfoReturnable<Integer> cir, int i, LootContext.Builder builder, LootTable lootTable, List<ItemStack> list) {
		ItemFishedEvent event = new ItemFishedEvent(list, this.field_7176 ? 2 : 1, (FishingBobberEntity) (Object) this);
		MinecraftForge.EVENT_BUS.post(event);

		if (event.isCanceled()) {
			this.remove();
			cir.setReturnValue(event.getRodDamage());
		} else {
			rodDamage.set(event.getRodDamage());
		}
	}

	@Inject(method = "method_6957", at = @At("RETURN"), cancellable = true)
	private void onRodDamage(ItemStack itemStack, CallbackInfoReturnable<Integer> cir) {
		Integer damage = rodDamage.get();

		if (damage != -1) {
			cir.setReturnValue(damage);
			rodDamage.remove();
		}
	}
}
