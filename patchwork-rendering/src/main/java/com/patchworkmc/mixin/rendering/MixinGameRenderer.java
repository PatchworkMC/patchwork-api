package com.patchworkmc.mixin.rendering;

import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(IIF)V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void beforeRenderScreen(float tickDelta, long startTime, boolean fullRender, CallbackInfo ci, int i, int j) {
		if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.DrawScreenEvent.Pre(client.currentScreen, i, j, tickDelta))) {
			ci.cancel();
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(IIF)V", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD)
	private void afterRenderScreen(float tickDelta, long startTime, boolean fullRender, CallbackInfo ci, int i, int j) {
		MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.DrawScreenEvent.Post(client.currentScreen, i, j, tickDelta));
	}
}
