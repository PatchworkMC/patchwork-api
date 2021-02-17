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

import java.util.List;
import java.util.function.Consumer;

import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;

import net.patchworkmc.impl.gui.ForgeScreen;

@Mixin(Screen.class)
public abstract class MixinScreen implements ForgeScreen {
	@Shadow
	@Final
	protected List<AbstractButtonWidget> buttons;

	@Shadow
	protected abstract <T extends AbstractButtonWidget> T addButton(T button);

	@Shadow
	@Final
	protected List<Element> children;

	@Shadow
	@Nullable
	protected MinecraftClient client;

	@Unique
	private final Consumer<AbstractButtonWidget> remove = (b) -> {
		buttons.remove(b);
		children.remove(b);
	};

	@Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At(value = "INVOKE", target = "Ljava/util/List;clear()V", ordinal = 0, remap = false), cancellable = true)
	private void preInit(MinecraftClient client, int width, int height, CallbackInfo info) {
		if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.InitGuiEvent.Pre((Screen) (Object) this, this.buttons, this::addButton, remove))) {
			info.cancel();
		}
	}

	@Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("RETURN"), cancellable = true)
	private void postInit(MinecraftClient client, int width, int height, CallbackInfo info) {
		MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.InitGuiEvent.Post((Screen) (Object) this, this.buttons, this::addButton, remove));
	}

	@Inject(method = "renderBackground(Lnet/minecraft/client/util/math/MatrixStack;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;fillGradient(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 0, shift = At.Shift.AFTER))
	private void renderBackground(MatrixStack matrices, int vOffset, CallbackInfo ci) {
		MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.BackgroundDrawnEvent((Screen) (Object) this, matrices));
	}

	@Override
	public MinecraftClient getMinecraft() {
		return this.client;
	}
}
