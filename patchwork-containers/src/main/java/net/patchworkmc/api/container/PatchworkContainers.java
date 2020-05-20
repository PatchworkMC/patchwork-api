package net.patchworkmc.api.container;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ContainerProvider;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

import net.patchworkmc.impl.container.PatchworkContainerType;
import net.patchworkmc.impl.container.PatchworkScreenProviderMap;

public final class PatchworkContainers {
	private PatchworkContainers() {
	}

	private static final Function<?, ContainerType<?>> CONTAINER_TYPE_FACTORY;
	private static final Logger LOGGER = LogManager.getLogger();

	static {
		try {
			MethodHandles.Lookup containerTypeLookup = ((PatchworkContainerType<?>) ContainerType.ANVIL).patchwork_getPrivateLookup();
			MappingResolver resolver = FabricLoader.getInstance().getMappingResolver();
			Class<?> containerFactoryClass = Class.forName(resolver.mapClassName("intermediary", "net.minecraft.class_3917$class_3918")); // ContainerType.Factory

			// We can't accessor the constructor due to the package-private interface in its arguments, so we use MethodHandles instead.
			// The metafactory call here is technically unnecessary as we could just call invoke on the MethodHandle directly, but it reduces the places we have to catch Throwable.
			MethodHandle ctor = containerTypeLookup.findConstructor(ContainerType.class,
					MethodType.methodType(void.class, containerFactoryClass));
			CONTAINER_TYPE_FACTORY = (Function<?, ContainerType<?>>) LambdaMetafactory.metafactory(containerTypeLookup,
					"apply", MethodType.methodType(Function.class), MethodType.methodType(Object.class, Object.class),
					ctor, MethodType.methodType(ContainerType.class, containerFactoryClass)).getTarget().invoke();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Constructs a {@link ContainerType} from a {@link PatchworkContainerFactory}.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Container> ContainerType<T> newContainerType(PatchworkContainerFactory<T> factory) {
		ContainerType<T> type = (ContainerType<T>) CONTAINER_TYPE_FACTORY.apply(null);
		((PatchworkContainerType<T>) type).patchwork_setContainerFactory(factory);
		return type;
	}

	public static <M extends Container, U extends Screen & ContainerProvider<M>> void registerScreenProvider(ContainerType<? extends M> type, PatchworkScreenProvider<M, U> provider) {
		return;
	}

	public static <T extends Container> Optional<PatchworkScreenProvider<T, ?>> getScreenProvider(@Nullable ContainerType<T> type, MinecraftClient mc, int windowId, Text title) {
		if (type == null) {
			LOGGER.warn("Trying to open invalid screen with name: {}", title.getString());
		} else {
			PatchworkScreenProvider<T, ?> iscreenfactory = PatchworkScreenProviderMap.getProvider(type);

			if (iscreenfactory == null) {
				LOGGER.warn("Failed to create screen for menu type: {}", Registry.CONTAINER.getId(type));
			} else {
				return Optional.of(iscreenfactory);
			}
		}

		return Optional.empty();
	}
}
