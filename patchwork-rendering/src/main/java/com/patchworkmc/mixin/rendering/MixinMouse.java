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
import net.minecraft.client.Mouse;

@Mixin(Mouse.class)
public abstract class MixinMouse {
	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "onMouseButton", at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screen/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V",
					ordinal = 0), locals = LocalCapture.PRINT, cancellable = true)
	public void preMouseClicked(long window, int button, int action, int mods, CallbackInfo info, boolean bl, int i, boolean[] bls, double d, double e) {
		bls[0] = bls[0] || MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseClickedEvent.Pre(client.currentScreen, d, e, button));
	}

	@Inject(method = "onMouseButton", at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screen/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V",
					ordinal = 0, shift = At.Shift.BY, by = 2), locals = LocalCapture.PRINT, cancellable = true)
	public void postMouseClicked(long window, int button, int action, int mods, CallbackInfo info, boolean bl, int i, boolean[] bls, double d, double e) {
		bls[0] = bls[0] || MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseClickedEvent.Post(client.currentScreen, d, e, button));
	}

	@Inject(method = "onMouseButton", at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screen/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V",
					ordinal = 1), locals = LocalCapture.PRINT, cancellable = true)
	public void preMouseReleased(long window, int button, int action, int mods, CallbackInfo info, boolean bl, int i, boolean[] bls, double d, double e) {
		bls[0] = bls[0] || MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseReleasedEvent.Pre(client.currentScreen, d, e, button));
	}

	@Inject(method = "onMouseButton", at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screen/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V",
					ordinal = 1, shift = At.Shift.BY, by = 2), locals = LocalCapture.PRINT, cancellable = true)
	public void postMouseReleased(long window, int button, int action, int mods, CallbackInfo info, boolean bl, int i, boolean[] bls, double d, double e) {
		bls[0] = bls[0] || MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseReleasedEvent.Post(client.currentScreen, d, e, button));
	}
}
