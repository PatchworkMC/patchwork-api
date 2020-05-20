package net.patchworkmc.mixin.container;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.Screens;
import net.minecraft.client.gui.screen.ingame.ContainerProvider;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import net.patchworkmc.api.container.PatchworkScreenProvider;
import net.patchworkmc.impl.container.PatchworkScreenProviderMap;

@Mixin(Screens.class)
public class MixinScreens {
	@SuppressWarnings("unchecked")
	@Inject(method = "register(Lnet/minecraft/container/ContainerType;Lnet/minecraft/client/gui/screen/Screens$Provider;)V",
			at = @At("HEAD"))
	private static <M extends Container, U extends Screen & ContainerProvider<M>> void registerCallback(ContainerType<? extends M> type, @Coerce Object provider, CallbackInfo callback) {
		System.out.println("PW register container " + Registry.CONTAINER.getId(type));
		PatchworkScreenProviderMap.registerVanilla(type, (ScreenProviderAccessor<M, U>) provider);
		// We don't cancel here, let vanilla do its logic. If the vanilla one can't open then we do ours, eliminates need for an overwrite.
	}

	@Inject(method = "open(Lnet/minecraft/container/ContainerType;Lnet/minecraft/client/MinecraftClient;ILnet/minecraft/text/Text;)V",
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/screen/Screens;getProvider(Lnet/minecraft/container/ContainerType;)Lnet/minecraft/client/gui/screen/Screens$Provider;"),
			cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private static <T extends Container> void open(@Nullable ContainerType<T> type, MinecraftClient client, int id, Text name, CallbackInfo callback, @Coerce Object provider) {
		PatchworkScreenProvider<T, ?> pwProvider;

		if (provider == null && (pwProvider = PatchworkScreenProviderMap.getProvider(type)) != null) {
			pwProvider.open(name, type, client, id);
			callback.cancel();
		}
	}
}
