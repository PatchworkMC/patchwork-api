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

package net.patchworkmc.impl.gui;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.ForgeIngameGui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;

public class PatchworkIngameGui {
	public static RenderGameOverlayEvent eventParent;

	/**
	 * These stores "snapshots" of the state of ForgeIngameGui.
	 *
	 * This allows ForgeIngameGui to be implemented without replacing
	 * the entirety of the status bar rendering, but keeping the
	 * event order intact.
	 */
	public static IngameGuiSnapshot preRenderHealthSnapshot;
	public static IngameGuiSnapshot preRenderArmorSnapshot;
	public static IngameGuiSnapshot preRenderFoodSnapshot;

	public static void fireGuiEvents(PlayerEntity player) {
		ForgeIngameGui.left_height = 39;
		ForgeIngameGui.right_height = 39;

		if (ForgeIngameGui.renderHealth) {
			fireHealthEvents(player);
		}

		if(ForgeIngameGui.renderArmor) {
			fireArmorEvents();
		}

		if (ForgeIngameGui.renderFood) {
			fireFoodEvents();
		}
	}

	private static boolean firePre(RenderGameOverlayEvent.ElementType elementType) {
		return MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(eventParent, elementType));
	}

	private static void firePost(RenderGameOverlayEvent.ElementType elementType) {
		MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(eventParent, elementType));
	}

	private static void fireHealthEvents(PlayerEntity player) {
		preRenderHealthSnapshot = new IngameGuiSnapshot(firePre(RenderGameOverlayEvent.ElementType.HEALTH));

		EntityAttributeInstance attrMaxHealth = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
		float maxHealth = (float) attrMaxHealth.getValue();
		float absorb = MathHelper.ceil(player.getAbsorptionAmount());

		int healthRows = MathHelper.ceil((maxHealth + absorb) / 2.0F / 10.0F);
		int rowHeight = Math.max(10 - (healthRows - 2), 3);

		ForgeIngameGui.left_height += (healthRows * rowHeight);

		if (rowHeight != 10) {
			ForgeIngameGui.left_height += 10 - rowHeight;
		}

		firePost(RenderGameOverlayEvent.ElementType.HEALTH);
	}

	private static void fireArmorEvents() {
		preRenderArmorSnapshot = new IngameGuiSnapshot(firePre(RenderGameOverlayEvent.ElementType.ARMOR));

		ForgeIngameGui.left_height += 10;

		firePost(RenderGameOverlayEvent.ElementType.ARMOR);
	}

	private static void fireFoodEvents() {
		preRenderFoodSnapshot = new IngameGuiSnapshot(firePre(RenderGameOverlayEvent.ElementType.FOOD));

		ForgeIngameGui.right_height += 10;

		firePost(RenderGameOverlayEvent.ElementType.FOOD);
	}
}
