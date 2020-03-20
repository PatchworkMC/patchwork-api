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

package net.minecraftforge.client.event.net.minecraftforge.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;

public class ForgeIngameGui extends InGameHud {
	//Flags to toggle the rendering of certain aspects of the HUD, valid conditions
	//must be met for them to render normally. If those conditions are met, but this flag
	//is false, they will not be rendered.
	//	public static boolean renderVignette = true;
	//	public static boolean renderHelmet = true;
	//	public static boolean renderPortal = true;
	//	public static boolean renderSpectatorTooltip = true;
	//	public static boolean renderHotbar = true;
	//	public static boolean renderCrosshairs = true;
	//	public static boolean renderBossHealth = true;
	public static boolean renderHealth = true;
	public static boolean renderArmor = true;
	public static boolean renderFood = true;
	public static boolean renderHealthMount = true;
	public static boolean renderAir = true;
	//	public static boolean renderExperiance = true;
	//	public static boolean renderJumpBar = true; // Don't forget to uncomment the setter in HookMainEvent
	//	public static boolean renderObjective = true;

	public static int left_height = 39;
	public static int right_height = 39;

	public ForgeIngameGui(MinecraftClient client) {
		super(client);
	}
}
