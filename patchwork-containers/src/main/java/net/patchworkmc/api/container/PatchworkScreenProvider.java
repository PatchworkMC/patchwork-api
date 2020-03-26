package net.patchworkmc.api.container;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ContainerProvider;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public interface PatchworkScreenProvider<T extends Container, U extends Screen & ContainerProvider<T>> {
	default void open(Text name, ContainerType<T> type, MinecraftClient client, int id) {
		U screen = create(type.create(id, client.player.inventory), client.player.inventory, name);
		client.player.container = screen.getContainer();
		client.openScreen(screen);
	}

	U create(T container, PlayerInventory playerInventory, Text text);
}
