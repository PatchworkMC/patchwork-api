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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.OutOfMemoryScreen;
import net.minecraft.client.gui.screen.Screen;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
	private static final String PATCHWORK_YARN_MTD_OPENSCREEN = "openScreen(Lnet/minecraft/client/gui/screen/Screen;)V";
	private static final String PATCHWORK_YARN_MTD_SCREEN_REMOVE = "net/minecraft/client/gui/screen/Screen.removed()V";

	// net.minecraft.client.gui.TitleScreen --> net.minecraft.class_442
	private static final String PATCHWORK_YARN_CLS_TITLESCREEN = "classValue=net/minecraft/client/gui/screen/TitleScreen";
	private static final String PATCHWORK_REOBF_CLS_TITLESCREEN = "classValue=net/minecraft/class_442";

	private static final Screen PATCHWORK_GUIOPENEVENT_CANCEL_MAKRER = new OutOfMemoryScreen();
	private static Screen patchwork_oldScreen;

	@Shadow
	public Screen currentScreen;

	@Redirect(method = PATCHWORK_YARN_MTD_OPENSCREEN, at = @At(value = "INVOKE", target = PATCHWORK_YARN_MTD_SCREEN_REMOVE))
	private void patchwork_suppressScreenRemoved(Screen screen) {
		// No-op (handled in patchwork_fireOpenEvent)
	}

	@ModifyVariable(method = PATCHWORK_YARN_MTD_OPENSCREEN, at = @At(value = "CONSTANT", args = PATCHWORK_YARN_CLS_TITLESCREEN, shift = Shift.BY, by = -2), require = 0)
	public Screen patchwork_yarn_fireOpenEvent(Screen screen) {
		return patchwork_impl_fireOpenEvent(screen);
	}

	@ModifyVariable(method = PATCHWORK_YARN_MTD_OPENSCREEN, at = @At(value = "CONSTANT", args = PATCHWORK_REOBF_CLS_TITLESCREEN, shift = Shift.BY, by = -2), require = 0)
	public Screen patchwork_reobf_fireOpenEvent(Screen screen) {
		return patchwork_impl_fireOpenEvent(screen);
	}

	@Inject(method = PATCHWORK_YARN_MTD_OPENSCREEN, at = @At(value = "CONSTANT", args = PATCHWORK_YARN_CLS_TITLESCREEN), cancellable = true, require = 0)
	public void patchwork_yarn_cancelOpening(Screen screen, CallbackInfo callback) {
		patchwork_impl_cancelOpening(screen, callback);
	}

	@Inject(method = PATCHWORK_YARN_MTD_OPENSCREEN, at = @At(value = "CONSTANT", args = PATCHWORK_REOBF_CLS_TITLESCREEN), cancellable = true, require = 0)
	public void patchwork_reobf_cancelOpening(Screen screen, CallbackInfo callback) {
		patchwork_impl_cancelOpening(screen, callback);
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
	private Screen patchwork_impl_fireOpenEvent(Screen screen) {
		// This is called just before: if (screen instanceof TitleScreen ... )
		// We need to save a copy of currentScreen, just in case if it gets changed during GuiOpenEvent
		patchwork_oldScreen = this.currentScreen;
		GuiOpenEvent event = new GuiOpenEvent(screen);

		if (MinecraftForge.EVENT_BUS.post(event)) {
			return PATCHWORK_GUIOPENEVENT_CANCEL_MAKRER;
		} else {
			return event.getGui();
		}
	}

	private void patchwork_impl_cancelOpening(Screen screen, CallbackInfo callback) {
		if (screen == PATCHWORK_GUIOPENEVENT_CANCEL_MAKRER) {
			patchwork_oldScreen = null;
			callback.cancel();
			return;
		}

		if (patchwork_oldScreen != null && screen != patchwork_oldScreen) {
			patchwork_oldScreen.removed();
		}

		patchwork_oldScreen = null;
	}
}
