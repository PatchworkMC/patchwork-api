package net.patchworkmc.impl.container;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ContainerProvider;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import net.patchworkmc.api.container.PatchworkScreenProvider;
import net.patchworkmc.mixin.container.ScreenProviderAccessor;

public class VanillaScreenProvider<T extends Container, U extends Screen & ContainerProvider<T>> implements PatchworkScreenProvider<T, U> {
	private final ScreenProviderAccessor<T, U> vanillaProvider;

	public VanillaScreenProvider(ScreenProviderAccessor<T, U> vanillaProvider) {
		this.vanillaProvider = vanillaProvider;
	}

	@Override
	public U create(T container, PlayerInventory playerInventory, Text text) {
		return vanillaProvider.invokeCreate(container, playerInventory, text);
	}
}
