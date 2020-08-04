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

package net.patchworkmc.mixin.extensions.item.client;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import net.patchworkmc.impl.extensions.item.PatchworkArmorItemHandler;

@Mixin(ArmorBipedFeatureRenderer.class)
public class MixinArmorBipedFeatureRenderer implements PatchworkArmorItemHandler {
	@SuppressWarnings("rawtypes")
	@Override
	public BipedEntityModel getArmorModelHook(LivingEntity entity, ItemStack itemStack, EquipmentSlot slot, BipedEntityModel defaultModel) {
		return PatchworkArmorItemHandler.patchwork$getArmorModel(entity, itemStack, slot, defaultModel);
	}
}
