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

import net.minecraftforge.client.ForgeIngameGui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.MathHelper;

import net.patchworkmc.impl.gui.PatchworkIngameGui;

/**
 * Implements events in {@link net.minecraftforge.client.ForgeIngameGui}.
 */
@Mixin(InGameHud.class)
public abstract class MixinInGameHud {
	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	private int scaledHeight;

	@Shadow
	protected abstract int method_1744(LivingEntity livingEntity);

	@Inject(method = "render", at = @At("HEAD"))
	private void registerEventParent(float tickDelta, CallbackInfo ci) {
		PatchworkIngameGui.eventParent = new RenderGameOverlayEvent(tickDelta, this.client.window);
	}

	// This fires all the events that are necessary for the status bars
	// The results of these events are handled later
	@Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;ceil(F)I", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	private void fireGuiEvents(CallbackInfo ci, PlayerEntity entity) {
		PatchworkIngameGui.fireStatusBarEvents(entity);
	}

	/**
	 * This disables the health status bar.
	 *
	 * <p>InGameHud contains the following for loop which renders the health bar:
	 * {@code for(z = MathHelper.ceil((f + (float)p) / 2.0F) - 1; z >= 0; --z)}</p>
	 *
	 * <p>This mixin redirects the MathHelper#ceil call. If the pre event is canceled,
	 * the returned value is 0, which will look like this:
	 * {@code for(z = 0 - 1; z >= 0; --z}</p>
	 *
	 * <p>0 - 1 is calculated, which leaves {@code z = -1}</p>
	 *
	 * <p>The next condition will fail, as {@code -1 >= 0} is not true,
	 * thus canceling the for loop and causing the health bar to not render</p>
	 */
	@Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;ceil(F)I", ordinal = 4))
	private int hookDisableHealthBar(float arg) {
		return (!ForgeIngameGui.renderHealth || PatchworkIngameGui.healthSnapshot.preResult) ? 0 : MathHelper.ceil(arg);
	}

	/**
	 * Properly hooks the left_height field for the health bar.
	 *
	 * <p>InGameHud renders the health bar with this method call:
	 * {@code this.blit(ad, ae, aa + 54, 9 * af, 9, 9)}</p>
	 *
	 * <p>This modifies the ae variable and replaces it with the
	 * proper value for ForgeIngameGui</p>
	 */
	@ModifyVariable(method = "renderStatusBars", at = @At(value = "CONSTANT", args = "intValue=4", shift = At.Shift.BEFORE), ordinal = 19)
	private int hookHealthLeftHeight(int originalValue) {
		return this.scaledHeight - PatchworkIngameGui.healthSnapshot.left_height;
	}

	/**
	 * This disables the armor status bar.
	 *
	 * <p>InGameHud contains the following for loop
	 * which renders the armor bar: {@code for(z = 0; z < 10; ++z)}</p>
	 *
	 *
	 * <p>This mixin modifies the 0 constant. If the pre event is canceled,
	 * the 0 constant is replaced with 10, so {@code z = 10}.
	 * The next condition, {@code z < 10} will fail as {@code 10 < 10} is not true,
	 * thus canceling the for loop and causing the armor bar to not render</p>
	 */
	@ModifyConstant(method = "renderStatusBars", constant = @Constant(intValue = 0, ordinal = 1))
	private int hookDisableArmor(int originalValue) {
		return (!ForgeIngameGui.renderArmor || PatchworkIngameGui.armorSnapshot.preResult) ? 10 : originalValue;
	}

	/**
	 * Properly hooks the left_height field for the armor bar.
	 *
	 * <p>InGameHud renders the armor bar with this method call: {@code this.blit(aa, s, 34, 9, 9, 9}</p>
	 *
	 * <p>This modifies the s variable and replaces it with the
	 * proper value for ForgeIngameGui</p>
	 */
	@ModifyVariable(method = "renderStatusBars", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", args = "ldc=armor"), ordinal = 9)
	private int hookArmorLeftHeight(int originalValue) {
		return this.scaledHeight - PatchworkIngameGui.armorSnapshot.left_height;
	}

	/**
	 * This disables the food bar.
	 *
	 * <p>InGameHud contains the following for loop which
	 * renders the food bar {@code for(ah = 0; ah < 10; ++ah)}</p>
	 *
	 * <p>This mixin modifies the 0 constant. If the pre event is canceled,
	 * the 0 constant is replaced with 10, so {@code ah = 10}.
	 * The next condition, {@code ah < 10} will fail as {@code 10 < 10} is not true,
	 * thus canceling the for loop and causing the food bar to not render</p>
	 */
	@ModifyConstant(method = "renderStatusBars", constant = @Constant(intValue = 0, ordinal = 4))
	private int hookDisableFood(int originalValue) {
		return (!ForgeIngameGui.renderFood || PatchworkIngameGui.foodSnapshot.preResult) ? 10 : originalValue;
	}

	/**
	 * Properly hooks the right_height field for the food bar.
	 *
	 * <p>InGameHud renders the food bar with this method call:
	 * {@code this.blit(al, ai, ad + 36, 27, 9, 9)}</p>
	 *
	 * <p>The ai variable is set to {@code ai = o}. This mixin
	 * modifies the o variable as it's needed later in the food
	 * bar.</p>
	 */
	@ModifyVariable(method = "renderStatusBars", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=food"), ordinal = 5)
	private int hookFoodRightHeight(int originalValue) {
		return this.scaledHeight - PatchworkIngameGui.foodSnapshot.right_height;
	}

	/**
	 * This helps disable the air bar.
	 *
	 * <p>InGameHud contains the following if statement
	 * to decide whether to render the air bar:
	 * {@code if (playerEntity.isInFluid(FluidTags.WATER) || ah < ai)}</p>
	 *
	 * <p>This mixin redirects the PlayerEntity#isInFluid call. If the
	 * pre-event is canceled, the returned value is false.</p>
	 *
	 * <p>The {@code ah < ai} condition is handled in {@link #modifyHookDisableAir}</p>
	 */
	@Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isInFluid(Lnet/minecraft/tag/Tag;)Z"))
	private boolean redirectHookDisableAir(PlayerEntity playerEntity, Tag<Fluid> fluidTag) {
		if (ForgeIngameGui.renderAir && PatchworkIngameGui.airSnapshot.preResult) {
			ForgeIngameGui.right_height += 10; // The bar will be rendered, so we need to increment right_height

			return false; // false means "we are not inside a fluid"
		}

		return playerEntity.isInFluid(fluidTag);
	}

	/**
	 * This helps disable the air bar.
	 *
	 * <p>InGameHud contains the following if statement
	 * to decide whether to render the air bar:
	 * {@code if (playerEntity.isInFluid(FluidTags.WATER) || ah < ai)}</p>
	 *
	 * <p>This mixin modifies ai, and replaces it with 0 if the event is canceled.
	 * The condition will then look like {@code ah < 0}
	 * ah is {@code playerEntity.getAir()}, which will be a positive value,
	 * so the condition will fail.</p>
	 *
	 * <p>The {@code playerEntity.isInFluid} condition is handled in {@link #redirectHookDisableAir}</p>
	 */
	@ModifyVariable(method = "renderStatusBars", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/player/PlayerEntity;getMaxAir()I"), ordinal = 13)
	private int modifyHookDisableAir(int originalValue) {
		return (!ForgeIngameGui.renderAir || PatchworkIngameGui.airSnapshot.preResult) ? 0 : originalValue;
	}

	/**
	 * Properly hooks the right_height field for the air bar.
	 *
	 * <p>InGameHud renders the air bar with this method call:
	 * {@code this.blit(n - ar * 8 - 9, t, 16, 18, 9, 9)}</p>
	 *
	 * <p>This modifies the t variable and replaces it with the
	 * proper value for ForgeIngameGui.</p>
	 *
	 * <p>This mixin injects right after {@code method_1733} is called,
	 * as t is then modified. The outputted mixin code looks like this:</p>
	 * <pre>
	 * {@code
	 *   ad = this.method_1733(aa) - 1;
	 * + // mixin here
	 *   t -= ad * 10;
	 *  }
	 * </pre>
	 */
	@ModifyVariable(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;ceil(D)I", ordinal = 0), ordinal = 10)
	private int hookAirRightHeight(int originalValue) {
		return this.scaledHeight - PatchworkIngameGui.foodSnapshot.right_height - 10;
	}

	/**
	 * This disables the mount health bar.
	 *
	 * <p>InGameHud contains the following if statement
	 * to decide whether to render the bar:</p>
	 * <pre>
	 * {@code
	 * int i = this.method_1744(livingEntity);
	 * if (i != 0) {
	 *  	// rendering code
	 * }
	 * }
	 * </pre>
	 *
	 * <p>This mixin redirects the {@code method_1744} call. If the event
	 * is canceled, the value that is returned is 0. The new check
	 * will then be {@code 0 != 0}, which obviously fails and causes the
	 * mount health bar to not render.</p>
	 */
	@Redirect(method = "renderMountHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;method_1744(Lnet/minecraft/entity/LivingEntity;)I"))
	private int hookDisableMountHealth(InGameHud inGameHud, LivingEntity livingEntity) {
		return (!ForgeIngameGui.renderHealthMount || PatchworkIngameGui.mountHealthSnapshot.preResult) ? 0 : this.method_1744(livingEntity);
	}

	/**
	 * This properly hooks the right_height field for the mount health bar.
	 *
	 * <p>InGameHud renders the mount health bar with this method call:
	 * {@code this.blit(s, m, 88, 9, 9, 9)}</p>
	 *
	 * <p>The m variable is equal to another variable, k, which is declared as:
	 * {@code int k = this.scaledHeight - 39}</p>
	 *
	 * <p>This mixin modifies the constant 39 value and replaces it with
	 * the right_height field.</p>
	 */
	@ModifyConstant(method = "renderMountHealth", constant = @Constant(intValue = 39))
	private int hookMountHealthRightHeight(int originalValue) {
		return PatchworkIngameGui.mountHealthSnapshot.right_height;
	}
}
