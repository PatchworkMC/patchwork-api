/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2020, 2019-2020
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.registries;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;

import net.minecraft.util.Identifier;

/**
 * Utility class to help with managing registry entries.
 * Maintains a list of all suppliers for entries and registers them during the proper Register event.
 * Suppliers should return NEW instances every time.
 * <p>
 * Example Usage:
 * <pre>
 *   private static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);
 *   private static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
 *
 *   public static final RegistryObject<Block> ROCK_BLOCK = BLOCKS.register("rock", () -> new Block(Block.Properties.create(Material.ROCK)));
 *   public static final RegistryObject<Item> ROCK_ITEM = ITEMS.register("rock", () -> new BlockItem(ROCK_BLOCK.get(), new Item.Properties().group(ItemGroup.MISC)));
 *
 *   public ExampleMod() {
 *       ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
 *       BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
 *   }
 * </pre>
 *
 * @param <T> The base registry type, must be a concrete base class, do not use subclasses or wild cards.
 */
public class DeferredRegister<T extends IForgeRegistryEntry<T>> {
	private final IForgeRegistry<T> type;
	private final String namespace;
	private final Map<RegistryObject<T>, Supplier<? extends T>> entries = new LinkedHashMap<>();
	private final Set<RegistryObject<T>> entriesView = Collections.unmodifiableSet(entries.keySet());

	public DeferredRegister(IForgeRegistry<T> registry, String namespace) {
		this.type = registry;
		this.namespace = namespace;
	}

	/**
	 * Adds a new supplier to the list of entries to be registered, and returns a RegistryObject that will be populated with the created entry automatically.
	 *
	 * @param name The new entry's name, it will automatically have the modid prefixed.
	 * @param supplier A factory for the new entry, it should return a new instance every time it is called.
	 * @return A RegistryObject that will be updated with when the entries in the registry change.
	 */
	@SuppressWarnings("unchecked")
	public <I extends T> RegistryObject<I> register(final String name, final Supplier<? extends I> supplier) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(supplier);

		final Identifier key = new Identifier(namespace, name);
		RegistryObject<I> registryObject = RegistryObject.of(key, this.type);

		if (entries.putIfAbsent((RegistryObject<T>) registryObject, setRegistryName(key, supplier)) != null) {
			throw new IllegalArgumentException("Duplicate registration " + name);
		}

		return registryObject;
	}

	/**
	 * Composes an existing supplier into one that sets the registry name of the supplied object.
	 *
	 * @param identifier The registry name that will be assigned to the supplied object
	 * @param supplier A factory for the new entry, it should return a new instance every time it is called.
	 * @return A new supplier that calls {@link IForgeRegistryEntry#setRegistryName(Identifier)}
	 */
	private <I extends T> Supplier<? extends T> setRegistryName(Identifier identifier, final Supplier<? extends I> supplier) {
		return () -> {
			T value = supplier.get();

			value.setRegistryName(identifier);

			return value;
		};
	}

	/**
	 * Adds our event handler to the specified event bus, this MUST be called in order for this class to function.
	 * See the example usage.
	 *
	 * @param bus The Mod Specific event bus.
	 */
	public void register(IEventBus bus) {
		bus.addListener(this::addEntries);
	}

	/**
	 * @return The unmodifiable view of registered entries. Useful for bulk operations on all values.
	 */
	public Collection<RegistryObject<T>> getEntries() {
		return entriesView;
	}

	private void addEntries(RegistryEvent.Register<?> event) {
		if (event.getGenericType() != this.type.getRegistrySuperType()) {
			return;
		}

		@SuppressWarnings("unchecked")
		IForgeRegistry<T> reg = (IForgeRegistry<T>) event.getRegistry();

		for (Entry<RegistryObject<T>, Supplier<? extends T>> entries : entries.entrySet()) {
			reg.register(entries.getValue().get());
			entries.getKey().updateReference(reg);
		}
	}
}
