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

package net.patchworkmc.mixin.extensions.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.World;

import net.patchworkmc.impl.extensions.item.PatchworkArmorItemHandler;

@Mixin(PlayerInventory.class)
public abstract class MixinPlayerInventory {
	@Inject(method = "updateItems", at = @At("RETURN"))
	private void fireArmorTick(CallbackInfo ci) {
		final PlayerInventory me = (PlayerInventory) (Object) this;
		final PlayerEntity player = me.player;
		final World world = player.world;
		me.armor.forEach(itemStack -> PatchworkArmorItemHandler.patchwork$fireArmorTick(itemStack, world, player));
	}
}
