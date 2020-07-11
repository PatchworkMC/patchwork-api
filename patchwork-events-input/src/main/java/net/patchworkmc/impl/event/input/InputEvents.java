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

package net.patchworkmc.impl.event.input;

import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import net.minecraft.client.Mouse;

import net.patchworkmc.api.input.ForgeMouse;

public class InputEvents {
	public static void fireMouseInput(int button, int action, int mods) {
		MinecraftForge.EVENT_BUS.post(new InputEvent.MouseInputEvent(button, action, mods));
	}

	public static void fireKeyInput(int key, int scanCode, int action, int modifiers) {
		MinecraftForge.EVENT_BUS.post(new InputEvent.KeyInputEvent(key, scanCode, action, modifiers));
	}

	public static boolean onMouseScroll(Mouse mouseHelper, double scrollDelta) {
		final Event event = new InputEvent.MouseScrollEvent(scrollDelta, mouseHelper.wasLeftButtonClicked(), ((ForgeMouse) mouseHelper).isMiddleDown(), mouseHelper.wasRightButtonClicked(), mouseHelper.getX(), mouseHelper.getY());

		return MinecraftForge.EVENT_BUS.post(event);
	}

	public static boolean onRawMouseClicked(int button, int action, int mods) {
		return MinecraftForge.EVENT_BUS.post(new InputEvent.RawMouseEvent(button, action, mods));
	}
}
