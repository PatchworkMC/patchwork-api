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

package net.minecraftforge.common.extensions;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.patchworkmc.annotations.Stubbed;

public interface IForgeEffect {
	default StatusEffect getEffect() {
		return (StatusEffect) this;
	}

	/**
	 * Returns true if the {@link StatusEffectInstance} should be displayed in the player's inventory.
	 *
	 * @param effect the active {@link StatusEffectInstance}
	 * @return true to display it (default), false to hide it.
	 */
	@Stubbed
	default boolean shouldRender(StatusEffectInstance effect) {
		return true;
	}

	/**
	 * Returns true if the {@link StatusEffectInstance} text (name and duration) should be drawn when this {@link StatusEffect} is active.
	 *
	 * @param effect the active {@link StatusEffectInstance}
	 * @return true to draw the standard text
	 */
	@Stubbed
	default boolean shouldRenderInvText(StatusEffectInstance effect) {
		return true;
	}

	/**
	 * Returns true if the {@link StatusEffectInstance} should be displayed in the player's {@link net.minecraft.client.gui.hud.InGameHud}.
	 *
	 * @param effect the active {@link StatusEffectInstance}
	 * @return true to display it (default), false to hide it.
	 */
	@Stubbed
	default boolean shouldRenderHUD(StatusEffectInstance effect) {
		return true;
	}

	/**
	 * Called to draw the {@link StatusEffectInstance} onto the player's inventory when it's active.
	 * This can be used to e.g. render {@link StatusEffect} icons from your own texture.
	 *
	 * @param effect the active {@link StatusEffectInstance}
	 * @param gui    the gui instance
	 * @param mStack the MatrixStack
	 * @param x      the x coordinate
	 * @param y      the y coordinate
	 * @param z      the z level
	 */
	@Stubbed
	@Environment(EnvType.CLIENT)
	default void renderInventoryEffect(StatusEffectInstance effect, AbstractInventoryScreen<?> gui, MatrixStack mStack, int x, int y, float z) {
	}

	/**
	 * Called to draw the {@link StatusEffectInstance} onto the player's {@link net.minecraft.client.gui.hud.InGameHud} when it's active.
	 * This can be used to e.g. render {@link StatusEffect} icons from your own texture.
	 *
	 * @param effect the active {@link StatusEffectInstance}
	 * @param gui    the gui instance
	 * @param mStack the MatrixStack
	 * @param x      the x coordinate
	 * @param y      the y coordinate
	 * @param z      the z level
	 * @param alpha  the alpha value, blinks when the {@link StatusEffect} is about to run out
	 */
	@Stubbed
	@Environment(EnvType.CLIENT)
	default void renderHUDEffect(StatusEffectInstance effect, DrawableHelper gui, MatrixStack mStack, int x, int y, float z, float alpha) {
	}

	/**
	 * Returns a fresh list of items that can cure this {@link StatusEffect}.
	 * All new {@link StatusEffectInstance}s created from this {@link StatusEffect} will call this to initialize the default curative items.
	 *
	 * @return A list of items that can cure this {@link StatusEffect}
	 */
	@Stubbed
	default List<ItemStack> getCurativeItems() {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		ret.add(new ItemStack(Items.MILK_BUCKET));
		return ret;
	}

	/**
	 * Used for determining {@link StatusEffectInstance} sort order in GUIs.
	 * Defaults to the {@link StatusEffectInstance}'s liquid color.
	 *
	 * @param effect the {@link StatusEffectInstance} containing the {@link StatusEffect}
	 * @return a value used to sort {@link StatusEffectInstance}s in GUIs
	 */
	@Stubbed
	default int getGuiSortColor(StatusEffectInstance effect) {
		return getEffect().getColor();
	}
}
