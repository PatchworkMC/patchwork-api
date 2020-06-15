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

package net.patchworkmc.mixin.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
	private static Screen nextScreen;

	/**
	 * Sets the argument Screen as the main (topmost visible) screen.
	 * <br>
	 * <strong>WARNING</strong>: This method is not thread-safe. Opening GUIs from a
	 * thread other than the main thread may cause many different issues, including
	 * the GUI being rendered before it has initialized (leading to unusual
	 * crashes). If on a thread other than the main thread, use
	 * {@link net.minecraft.client.MinecraftClient#executeTask}:
	 *
	 * <pre>
	 * MinecraftClient.getInstance().executeTask(() -> MinecraftClient.getInstance().openScreen(screen));
	 * </pre>
	 */
	@Inject(at = @At("HEAD"), cancellable = true, method = "openScreen(Lnet/minecraft/client/gui/screen/Screen;)V")
	public void handleOpenScreen(Screen screen, CallbackInfo info) {
		GuiOpenEvent event = new GuiOpenEvent(screen);

		if (MinecraftForge.EVENT_BUS.post(event)) {
			info.cancel();
			nextScreen = null;
		}

		nextScreen = event.getGui();
	}

	@ModifyVariable(at = @At("HEAD"), method = "openScreen(Lnet/minecraft/client/gui/screen/Screen;)V")
	private Screen patchArg(Screen screen) {
		if (nextScreen != null) {
			screen = nextScreen;
			nextScreen = null;
		}

		return screen;
	}
}
