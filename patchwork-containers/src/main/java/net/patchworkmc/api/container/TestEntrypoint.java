package net.patchworkmc.api.container;

import net.minecraft.container.AnvilContainer;
import net.minecraft.entity.player.PlayerInventory;

import net.fabricmc.api.ModInitializer;

public class TestEntrypoint implements ModInitializer {
	@Override
	public void onInitialize() {
		PatchworkContainers.newContainerType(new Test()).create(0, null);
	}

	private class Test implements PatchworkContainerFactory<AnvilContainer> {
		@Override
		public AnvilContainer create(int syncId, PlayerInventory playerInventory) {
			System.out.println("SANS");
			return null;
		}
	}
}
