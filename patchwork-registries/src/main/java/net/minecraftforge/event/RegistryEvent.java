package net.minecraftforge.event;

import net.minecraft.util.Identifier;
import net.minecraftforge.eventbus.api.GenericEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RegistryEvent<T> extends GenericEvent<T> {
	// Required for post() to work
	public RegistryEvent() {
		this(null);
	}

	RegistryEvent(Class<T> clazz) {
		super(clazz);
	}

	public static class Register<V extends IForgeRegistryEntry<V>> extends RegistryEvent<V> {
		private final IForgeRegistry<V> registry;
		private final Identifier name;

		// Required for post() to work
		public Register() {
			super(null);

			this.registry = null;
			this.name = null;
		}

		public Register(IForgeRegistry<V> registry) {
			this(registry.getRegistryName(), registry);
		}

		public Register(Identifier name, IForgeRegistry<V> registry) {
			super(registry.getRegistrySuperType());
			this.name = name;
			this.registry = registry;
		}

		public IForgeRegistry<V> getRegistry() {
			return registry;
		}

		public Identifier getName() {
			return name;
		}
	}
}
