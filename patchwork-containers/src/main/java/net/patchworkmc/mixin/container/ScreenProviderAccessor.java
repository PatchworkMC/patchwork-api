package net.patchworkmc.mixin.container;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ContainerProvider;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

@Mixin(targets = "net.minecraft.client.gui.screen.Screens$Provider")
public interface ScreenProviderAccessor<T extends Container, U extends Screen & ContainerProvider<T>> {
	@Invoker
	U invokeCreate(T container, PlayerInventory playerInventory, Text text);
}
