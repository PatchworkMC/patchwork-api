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

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At.Shift;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.patchworkmc.impl.event.entity.PlayerEvents;

@Mixin(CraftingResultSlot.class)
public abstract class MixinCraftingResultSlot {
	@Shadow
	@Final
	private PlayerEntity player;

	@Shadow
	@Final
	private CraftingInventory craftingInv;

	@Inject(method = "onCrafted(Lnet/minecraft/item/ItemStack;)V",
			at = @At(
					value = "INVOKE",
					shift = Shift.AFTER,
					ordinal = 0,
					target = "net/minecraft/item/ItemStack.onCraft(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;I)V"
					)
	)
	private void onStackCrafted(ItemStack stack, CallbackInfo ci) {
		PlayerEvents.firePlayerCraftingEvent(player, stack, craftingInv);
	}
}
