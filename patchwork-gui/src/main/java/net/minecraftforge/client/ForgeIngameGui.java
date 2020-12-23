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

/**
 * No logic is performed inside ForgeIngameGui,
 * as it invasively replaces the entirety of InGameHud.
 * This class only exists for compatibility.
 *
 * <p>Instead, logic is handled inside {@link net.patchworkmc.impl.gui.PatchworkIngameGui}
 * and {@link net.patchworkmc.mixin.gui.MixinInGameHud}.</p>
 */
public class ForgeIngameGui {
	public static boolean renderHealth = true;
	public static boolean renderArmor = true;
	public static boolean renderFood = true;
	public static boolean renderHealthMount = true;
	public static boolean renderAir = true;

	// TODO: Implement these
	public static boolean renderExperiance = true;
	public static boolean renderJumpBar = true;
	public static boolean renderObjective = true;
	public static boolean renderVignette = true;
	public static boolean renderHelmet = true;
	public static boolean renderPortal = true;
	public static boolean renderSpectatorTooltip = true;
	public static boolean renderHotbar = true;
	public static boolean renderCrosshairs = true;
	public static boolean renderBossHealth = true;

	public static int left_height = 39;
	public static int right_height = 39;
}
