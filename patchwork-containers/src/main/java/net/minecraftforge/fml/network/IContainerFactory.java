package net.minecraftforge.fml.network;

import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.PacketByteBuf;

import net.patchworkmc.api.container.PatchworkContainerFactory;

public interface IContainerFactory<T extends Container> extends PatchworkContainerFactory<T> {
	T create(int windowId, PlayerInventory inv, PacketByteBuf data);

	@Override
	default T create(int syncId, PlayerInventory playerInventory) {
		return create(syncId, playerInventory, null);
	}
}
