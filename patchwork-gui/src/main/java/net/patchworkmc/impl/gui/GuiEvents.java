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

import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;

import net.patchworkmc.api.gui.ForgeScreen;

// TODO: These events are not actually being fired yet -- used in ForgeHooksClient implementation.
public class GuiEvents {
	public static void drawScreen(Screen screen, int mouseX, int mouseY, float partialTicks) {
		if (!MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.DrawScreenEvent.Pre(screen, mouseX, mouseY, partialTicks))) {
			screen.render(mouseX, mouseY, partialTicks);
		}

		MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.DrawScreenEvent.Post(screen, mouseX, mouseY, partialTicks));
	}

	public static boolean onGuiMouseClickedPre(Screen guiScreen, double mouseX, double mouseY, int button) {
		Event event = new GuiScreenEvent.MouseClickedEvent.Pre(guiScreen, mouseX, mouseY, button);
		return MinecraftForge.EVENT_BUS.post(event);
	}

	public static boolean onGuiMouseClickedPost(Screen guiScreen, double mouseX, double mouseY, int button) {
		Event event = new GuiScreenEvent.MouseClickedEvent.Post(guiScreen, mouseX, mouseY, button);
		return MinecraftForge.EVENT_BUS.post(event);
	}

	public static boolean onGuiMouseReleasedPre(Screen guiScreen, double mouseX, double mouseY, int button) {
		Event event = new GuiScreenEvent.MouseReleasedEvent.Pre(guiScreen, mouseX, mouseY, button);
		return MinecraftForge.EVENT_BUS.post(event);
	}

	public static boolean onGuiMouseReleasedPost(Screen guiScreen, double mouseX, double mouseY, int button) {
		Event event = new GuiScreenEvent.MouseReleasedEvent.Post(guiScreen, mouseX, mouseY, button);
		return MinecraftForge.EVENT_BUS.post(event);
	}

	public static boolean onGuiMouseDragPre(Screen guiScreen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
		Event event = new GuiScreenEvent.MouseDragEvent.Pre(guiScreen, mouseX, mouseY, mouseButton, dragX, dragY);
		return MinecraftForge.EVENT_BUS.post(event);
	}

	public static boolean onGuiMouseDragPost(Screen guiScreen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
		Event event = new GuiScreenEvent.MouseDragEvent.Post(guiScreen, mouseX, mouseY, mouseButton, dragX, dragY);
		return MinecraftForge.EVENT_BUS.post(event);
	}

	public static boolean onGuiMouseScrollPre(Mouse mouseHelper, Screen guiScreen, double scrollDelta) {
		Window mainWindow = ((ForgeScreen) guiScreen).getMinecraft().window;
		double mouseX = mouseHelper.getX() * (double) mainWindow.getScaledWidth() / (double) mainWindow.getWidth();
		double mouseY = mouseHelper.getY() * (double) mainWindow.getScaledHeight() / (double) mainWindow.getHeight();
		Event event = new GuiScreenEvent.MouseScrollEvent.Pre(guiScreen, mouseX, mouseY, scrollDelta);
		return MinecraftForge.EVENT_BUS.post(event);
	}

	public static boolean onGuiMouseScrollPost(Mouse mouseHelper, Screen guiScreen, double scrollDelta) {
		Window mainWindow = ((ForgeScreen) guiScreen).getMinecraft().window;
		double mouseX = mouseHelper.getX() * (double) mainWindow.getScaledWidth() / (double) mainWindow.getWidth();
		double mouseY = mouseHelper.getY() * (double) mainWindow.getScaledHeight() / (double) mainWindow.getHeight();
		Event event = new GuiScreenEvent.MouseScrollEvent.Post(guiScreen, mouseX, mouseY, scrollDelta);
		return MinecraftForge.EVENT_BUS.post(event);
	}

	public static boolean onGuiKeyPressedPre(Screen guiScreen, int keyCode, int scanCode, int modifiers) {
		Event event = new GuiScreenEvent.KeyboardKeyPressedEvent.Pre(guiScreen, keyCode, scanCode, modifiers);
		return MinecraftForge.EVENT_BUS.post(event);
	}

	public static boolean onGuiKeyPressedPost(Screen guiScreen, int keyCode, int scanCode, int modifiers) {
		Event event = new GuiScreenEvent.KeyboardKeyPressedEvent.Post(guiScreen, keyCode, scanCode, modifiers);
		return MinecraftForge.EVENT_BUS.post(event);
	}

	public static boolean onGuiKeyReleasedPre(Screen guiScreen, int keyCode, int scanCode, int modifiers) {
		Event event = new GuiScreenEvent.KeyboardKeyReleasedEvent.Pre(guiScreen, keyCode, scanCode, modifiers);
		return MinecraftForge.EVENT_BUS.post(event);
	}

	public static boolean onGuiKeyReleasedPost(Screen guiScreen, int keyCode, int scanCode, int modifiers) {
		Event event = new GuiScreenEvent.KeyboardKeyReleasedEvent.Post(guiScreen, keyCode, scanCode, modifiers);
		return MinecraftForge.EVENT_BUS.post(event);
	}

	public static boolean onGuiCharTypedPre(Screen guiScreen, char codePoint, int modifiers) {
		Event event = new GuiScreenEvent.KeyboardCharTypedEvent.Pre(guiScreen, codePoint, modifiers);
		return MinecraftForge.EVENT_BUS.post(event);
	}

	public static boolean onGuiCharTypedPost(Screen guiScreen, char codePoint, int modifiers) {
		Event event = new GuiScreenEvent.KeyboardCharTypedEvent.Post(guiScreen, codePoint, modifiers);
		return MinecraftForge.EVENT_BUS.post(event);
	}
}
