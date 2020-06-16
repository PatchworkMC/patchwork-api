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

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
	private static final String methodOpenScreen = "openScreen(Lnet/minecraft/client/gui/screen/Screen;)V";
	private static final String methodScreenRemoved = "net/minecraft/client/gui/screen/Screen.removed()V";
	private static final String fieldCurrentScreen = "net/minecraft/client/MinecraftClient.currentScreen:Lnet/minecraft/client/gui/screen/Screen;";
	private static final String constTitleScreenClass = "classValue=net/minecraft/client/gui/screen/TitleScreen";

	private static boolean patchwork_openScreenCancelled;

	@Shadow
	public Screen currentScreen;

	@Redirect(method = methodOpenScreen, at = @At(value = "INVOKE", target = methodScreenRemoved))
	private void patchwork_suppressScreenRemoved(Screen screen) {
		// No-op (handled in patchwork_fireOpenEvent)
	}

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
	@ModifyVariable(method = methodOpenScreen, at = @At(value = "CONSTANT", args = constTitleScreenClass))
	public Screen patchwork_fireOpenEvent(Screen screen) {
		// This is called just before: if (screen instanceof TitleScreen ... )
		Screen old = this.currentScreen;
		GuiOpenEvent event = new GuiOpenEvent(screen);

		if (MinecraftForge.EVENT_BUS.post(event)) {
			patchwork_openScreenCancelled = true;
			return screen;
		}

		patchwork_openScreenCancelled = false;

		screen = event.getGui();

		if (old != null && screen != old) {
			old.removed();
		}

		return screen;
	}

	@Inject(method = methodOpenScreen, at = @At(value = "FIELD", target = fieldCurrentScreen, opcode = Opcodes.PUTFIELD), cancellable = true)
	public void patchwork_cancelOpening(Screen currentScreen, CallbackInfo callback) {
		if (patchwork_openScreenCancelled) {
			patchwork_openScreenCancelled = false;
			callback.cancel();
		}
	}
}
