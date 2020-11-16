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

package net.patchworkmc.impl.extensions.item;

import javax.annotation.Nullable;

import net.minecraftforge.common.extensions.IForgeItem;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import net.patchworkmc.annotations.GodClass;

public interface PatchworkArmorItemHandler {
	@SuppressWarnings("rawtypes")
	BipedEntityModel getArmorModelHook(LivingEntity entity, ItemStack itemStack, EquipmentSlot slot, BipedEntityModel model);

	Identifier getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type);

	/**
	 * Called by mixins(MixinArmorFeatureRenderer) and ForgeHooksClient.
	 */
	@GodClass(value = "net.minecraftforge.client.ForgeHooksClient", name = "getArmorTexture")
	static String patchwork$getArmorTexture(Entity entity, ItemStack itemStack, String defaultTexture, EquipmentSlot slot, String type) {
		IForgeItem forgeItem = (IForgeItem) itemStack.getItem();
		String result = forgeItem.getArmorTexture(itemStack, entity, slot, type);
		return result != null ? result : defaultTexture;
	}

	@GodClass(value = "net.minecraftforge.client.ForgeHooksClient", name = "getArmorModel")
	static <A extends BipedEntityModel<?>> A patchwork$getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot slot, A _default) {
		IForgeItem forgeItem = (IForgeItem) itemStack.getItem();
		A model = forgeItem.getArmorModel(entityLiving, itemStack, slot, _default);
		return model == null ? _default : model;
	}

	/**
	 * Called by mixins(MixinPlayerInventory) and IForgeItemStack.
	 */
	static void patchwork$fireArmorTick(ItemStack itemStack, World world, PlayerEntity player) {
		IForgeItem item = (IForgeItem) itemStack.getItem();
		item.onArmorTick(itemStack, world, player);
	}
}
