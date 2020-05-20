package net.patchworkmc.api.container;

import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Accessible "copy" of {@link net.minecraft.container.ContainerType.Factory}.
 */
@FunctionalInterface
public interface PatchworkContainerFactory<T extends Container> {
	@Environment(EnvType.CLIENT)
	T create(int syncId, PlayerInventory playerInventory);
}
