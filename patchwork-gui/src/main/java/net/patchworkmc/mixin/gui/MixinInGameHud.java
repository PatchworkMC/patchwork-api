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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.ForgeIngameGui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.patchworkmc.impl.gui.PatchworkInGameGui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {
	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "render", at = @At("HEAD"))
	private void registerEventParent(float tickDelta, CallbackInfo ci) {
		PatchworkInGameGui.eventParent = new RenderGameOverlayEvent(tickDelta, this.client.window);
	}

	// This fires all the Pre- events that are necessary for the status bars
	// The results of these events are handled later
	@Inject(method = "renderStatusBars", at = @At("HEAD"))
	private void fireGuiPreEvents(CallbackInfo ci) {
		PatchworkInGameGui.fireGuiPreEvents();
	}

	/**
	 * This disables the health status bar.
	 *
	 * InGameHud contains the following for loop which renders the health bar:
	 * {@code for(z = MathHelper.ceil((f + (float)p) / 2.0F) - 1; z >= 0; --z)}
	 *
	 * This mixin redirects the MathHelper#ceil call. If the pre event is canceled,
	 * the returned value is 0, which will look like this:
	 * {@code for(z = 0 - 1; z >= 0; --z}
	 *
	 * 0 - 1 is calculated, which leaves {@code z = -1}
	 *
	 * The next condition will fail, as -1 >= 0 is not true,
	 * thus canceling the for loop and causing the health bar to not render
	 */
	@Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;ceil(F)I", ordinal = 4))
	private int hookDisableHealthBar(float arg) {
		return ForgeIngameGui.renderHealth && PatchworkInGameGui.preRenderHealthResult ? 0 : MathHelper.ceil(arg);
	}

	/**
	 * This disables the armor status bar.
	 *
	 * InGameHud contains the following for loop which renders the armor bar:
	 * {@code for(z = 0; z < 10; ++z)}
	 *
	 * This mixin modifies the 0 constant. If the pre event is canceled,
	 * the 0 constant is replaced with 10, so {@code z = 10}.
	 * The next condition, {@code z < 10} will fail as {@code 10 < 10} is not true,
	 * thus canceling the for loop and causing the armor bar to not render
	 */
	@ModifyConstant(method = "renderStatusBars", constant = @Constant(intValue = 0, ordinal = 1))
	private int hookDisableArmor(int originalValue) {
		return ForgeIngameGui.renderArmor && PatchworkInGameGui.preRenderArmorResult ? 10 : originalValue;
	}

	/**
	 * This disables the food bar.
	 *
	 * InGameHud contains the following for loop which renders the food bar:
	 * {@code for(ah = 0; ah < 10; ++ah)}
	 *
	 * This mixin modifies the 0 constant. If the pre event is canceled,
	 * the 0 constant is replaced with 10, so {@code ah = 10}.
	 * The next condition, {@code ah < 10} will fail as {@code 10 < 10} is not true,
	 * thus canceling the for loop and causing the food bar to not render
	 */
	@ModifyConstant(method = "renderStatusBars", constant = @Constant(intValue = 0, ordinal = 4))
	private int hookDisableFood(int originalValue) {
		return ForgeIngameGui.renderFood && PatchworkInGameGui.preRenderFoodResult ? 10 : originalValue;
	}
}
