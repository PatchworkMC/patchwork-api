package net.patchworkmc.impl.container;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ContainerProvider;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.util.registry.Registry;

import net.patchworkmc.api.container.PatchworkScreenProvider;
import net.patchworkmc.mixin.container.ScreenProviderAccessor;

public class PatchworkScreenProviderMap {
	private static final Map<ContainerType<?>, PatchworkScreenProvider<?, ?>> PROVIDERS = new HashMap<>();

	public static <M extends Container, U extends Screen & ContainerProvider<M>> void registerVanilla(ContainerType<? extends M> type, ScreenProviderAccessor<M, U> provider) {
		if (PROVIDERS.put(type, new VanillaScreenProvider<M, U>(provider)) != null) {
			throw new IllegalStateException("Duplicate registration for " + Registry.CONTAINER.getId(type));
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Container> PatchworkScreenProvider<T, ?> getProvider(ContainerType<T> type) {
		return (PatchworkScreenProvider<T, ?>) PROVIDERS.get(type);
	}
}
