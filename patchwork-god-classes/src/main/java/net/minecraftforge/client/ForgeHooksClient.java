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

package net.minecraftforge.client;

import java.util.Set;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.client.Mouse;

import net.patchworkmc.impl.event.input.InputEvents;
import net.patchworkmc.impl.event.render.RenderEvents;
import net.patchworkmc.impl.extensions.item.PatchworkArmorItemHandler;

/*
 * Note: this class is intended for mod use only, to dispatch to the implementations kept in their own modules.
 * Do not keep implementation details here, methods should be thin wrappers around methods in other modules.
 */
public class ForgeHooksClient {
	public static String getArmorTexture(Entity entity, ItemStack armor, String defaultTexture, EquipmentSlot slot, String type) {
		return PatchworkArmorItemHandler.patchwork$getArmorTexture(entity, armor, defaultTexture, slot, type);
	}

	public static <A extends BipedEntityModel<?>> A getArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot slot, A defaultModel) {
		return PatchworkArmorItemHandler.patchwork$getArmorModel(livingEntity, itemStack, slot, defaultModel);
	}

	public static void fireMouseInput(int button, int action, int mods) {
		InputEvents.fireMouseInput(button, action, mods);
	}

	public static void fireKeyInput(int key, int scanCode, int action, int modifiers) {
		InputEvents.fireKeyInput(key, scanCode, action, modifiers);
	}

	public static boolean onMouseScroll(Mouse mouseHelper, double scrollDelta) {
		return InputEvents.onMouseScroll(mouseHelper, scrollDelta);
	}

	public static boolean onRawMouseClicked(int button, int action, int mods) {
		return InputEvents.onRawMouseClicked(button, action, mods);
	}

	public static void onBlockColorsInit(BlockColors blockColors) {
		RenderEvents.onBlockColorsInit(blockColors);
	}

	public static void onItemColorsInit(ItemColors itemColors, BlockColors blockColors) {
		RenderEvents.onItemColorsInit(itemColors, blockColors);
	}

	public static void onTextureStitchedPre(SpriteAtlasTexture map, Set<Identifier> resourceLocations) {
		RenderEvents.onTextureStitchPre(map, resourceLocations);
	}

	public static void onTextureStitchedPost(SpriteAtlasTexture map) {
		RenderEvents.onTextureStitchPost(map);
	}
}
