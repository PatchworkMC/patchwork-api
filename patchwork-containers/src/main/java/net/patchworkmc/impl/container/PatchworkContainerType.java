package net.patchworkmc.impl.container;

import java.lang.invoke.MethodHandles;

import net.minecraft.container.Container;

import net.patchworkmc.api.container.PatchworkContainerFactory;

public interface PatchworkContainerType<T extends Container> {
	void patchwork_setContainerFactory(PatchworkContainerFactory<T> factory);

	MethodHandles.Lookup patchwork_getPrivateLookup();
}
