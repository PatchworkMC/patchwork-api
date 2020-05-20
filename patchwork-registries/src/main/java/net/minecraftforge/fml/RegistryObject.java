/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.fml;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

import net.minecraft.util.Identifier;

import net.patchworkmc.impl.registries.RegistrationCompleteCallback;

public final class RegistryObject<T extends IForgeRegistryEntry<? super T>> implements Supplier<T> {
	private static RegistryObject<?> EMPTY = new RegistryObject<>();
	private final Identifier identifier;
	@Nullable
	private T value;

	private RegistryObject() {
		this.identifier = null;
		this.value = null;
	}

	private <V extends IForgeRegistryEntry<V>> RegistryObject(Identifier identifier, Supplier<Class<? super V>> registryType) {
		this(identifier, RegistryManager.ACTIVE.<V>getRegistry(registryType.get()));
	}

	@SuppressWarnings("unchecked")
	private <V extends IForgeRegistryEntry<V>> RegistryObject(Identifier identifier, IForgeRegistry<V> registry) {
		Objects.requireNonNull(identifier, "Invalid name argument, must not be null");
		Objects.requireNonNull(registry, "Invalid registry argument, must not be null");

		this.identifier = identifier;

		/*if (registry instanceof ForgeRegistry) {
			Registry<V> vanilla = ((ForgeRegistry<V>) registry).getVanilla();
			Consumer<V> updater = value -> this.value = (T) value;

			ObjectHolderRegistry.INSTANCE.register(vanilla, identifier.getNamespace(), identifier.getPath(), updater);
		} else {
			// TODO: Use normal path for vanilla registries, just testing the fallback path for now
		}*/

		RegistrationCompleteCallback.EVENT.register (
				() -> this.value = registry.containsKey(this.identifier) ? (T) registry.getValue(this.identifier) : null
		);
	}

	@Deprecated
	public static <T extends IForgeRegistryEntry<T>, U extends T> RegistryObject<U> of(final String name, Supplier<Class<? super T>> registryType) {
		return of(new Identifier(name), registryType);
	}

	public static <T extends IForgeRegistryEntry<T>, U extends T> RegistryObject<U> of(final Identifier name, Supplier<Class<? super T>> registryType) {
		return new RegistryObject<>(name, registryType);
	}

	@Deprecated
	public static <T extends IForgeRegistryEntry<T>, U extends T> RegistryObject<U> of(final String name, IForgeRegistry<T> registry) {
		return of(new Identifier(name), registry);
	}

	public static <T extends IForgeRegistryEntry<T>, U extends T> RegistryObject<U> of(final Identifier name, IForgeRegistry<T> registry) {
		return new RegistryObject<>(name, registry);
	}

	private static <T extends IForgeRegistryEntry<? super T>> RegistryObject<T> empty() {
		@SuppressWarnings("unchecked")
		RegistryObject<T> t = (RegistryObject<T>) EMPTY;
		return t;
	}

	/**
	 * Directly retrieves the wrapped Registry Object. This value will automatically be updated when the backing registry is updated.
	 */
	@Nullable
	@Override
	public T get() {
		return this.value;
	}

	public void updateReference(IForgeRegistry<? extends T> registry) {
		this.value = registry.getValue(getId());
	}

	public Identifier getId() {
		return this.identifier;
	}

	/**
	 * @deprecated Prefer {@link #getId()}
	 */
	@Deprecated
	public String getName() {
		return getId().toString();
	}

	public Stream<T> stream() {
		return isPresent() ? Stream.of(get()) : Stream.of();
	}

	/**
	 * Return {@code true} if there is a mod object present, otherwise {@code false}.
	 *
	 * @return {@code true} if there is a mod object present, otherwise {@code false}
	 */
	public boolean isPresent() {
		return get() != null;
	}

	/**
	 * If a mod object is present, invoke the specified consumer with the object,
	 * otherwise do nothing.
	 *
	 * @param consumer block to be executed if a mod object is present
	 * @throws NullPointerException if mod object is present and {@code consumer} is
	 *                              null
	 */
	public void ifPresent(Consumer<? super T> consumer) {
		if (get() != null) {
			consumer.accept(get());
		}
	}

	/**
	 * If a mod object is present, and the mod object matches the given predicate,
	 * return an {@code RegistryObject} describing the value, otherwise return an
	 * empty {@code RegistryObject}.
	 *
	 * @param predicate a predicate to apply to the mod object, if present
	 * @return an {@code RegistryObject} describing the value of this {@code RegistryObject}
	 * if a mod object is present and the mod object matches the given predicate,
	 * otherwise an empty {@code RegistryObject}
	 * @throws NullPointerException if the predicate is null
	 */
	public RegistryObject<T> filter(Predicate<? super T> predicate) {
		Objects.requireNonNull(predicate);

		if (!isPresent()) {
			return this;
		} else {
			return predicate.test(get()) ? this : empty();
		}
	}

	/**
	 * If a mod object is present, apply the provided mapping function to it,
	 * and if the result is non-null, return an {@code Optional} describing the
	 * result.  Otherwise return an empty {@code Optional}.
	 *
	 * @param <U>    The type of the result of the mapping function
	 * @param mapper a mapping function to apply to the mod object, if present
	 * @return an {@code Optional} describing the result of applying a mapping
	 * function to the mod object of this {@code RegistryObject}, if a mod object is present,
	 * otherwise an empty {@code Optional}
	 * @throws NullPointerException if the mapping function is null
	 * @apiNote This method supports post-processing on optional values, without
	 * the need to explicitly check for a return status.
	 */
	public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
		Objects.requireNonNull(mapper);

		if (!isPresent()) {
			return Optional.empty();
		} else {
			return Optional.ofNullable(mapper.apply(get()));
		}
	}

	/**
	 * If a value is present, apply the provided {@code Optional}-bearing
	 * mapping function to it, return that result, otherwise return an empty
	 * {@code Optional}.  This method is similar to {@link #map(Function)},
	 * but the provided mapper is one whose result is already an {@code Optional},
	 * and if invoked, {@code flatMap} does not wrap it with an additional
	 * {@code Optional}.
	 *
	 * @param <U>    The type parameter to the {@code Optional} returned by
	 * @param mapper a mapping function to apply to the mod object, if present
	 *               the mapping function
	 * @return the result of applying an {@code Optional}-bearing mapping
	 * function to the value of this {@code Optional}, if a value is present,
	 * otherwise an empty {@code Optional}
	 * @throws NullPointerException if the mapping function is null or returns
	 *                              a null result
	 */
	public <U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper) {
		Objects.requireNonNull(mapper);

		if (!isPresent()) {
			return Optional.empty();
		} else {
			return Objects.requireNonNull(mapper.apply(get()));
		}
	}

	/**
	 * If a mod object is present, lazily apply the provided mapping function to it,
	 * returning a supplier for the transformed result. If this object is empty, or the
	 * mapping function returns {@code null}, the supplier will return {@code null}.
	 *
	 * @param <U>    The type of the result of the mapping function
	 * @param mapper A mapping function to apply to the mod object, if present
	 * @return A {@code Supplier} lazily providing the result of applying a mapping
	 * function to the mod object of this {@code RegistryObject}, if a mod object is present,
	 * otherwise a supplier returning {@code null}
	 * @throws NullPointerException if the mapping function is {@code null}
	 * @apiNote This method supports post-processing on optional values, without
	 * the need to explicitly check for a return status.
	 */
	public <U> Supplier<U> lazyMap(Function<? super T, ? extends U> mapper) {
		Objects.requireNonNull(mapper);
		return () -> isPresent() ? mapper.apply(get()) : null;
	}

	/**
	 * Return the mod object if present, otherwise return {@code other}.
	 *
	 * @param other the mod object to be returned if there is no mod object present, may
	 *              be null
	 * @return the mod object, if present, otherwise {@code other}
	 */
	public T orElse(T other) {
		return isPresent() ? get() : other;
	}

	/**
	 * Return the mod object if present, otherwise invoke {@code other} and return
	 * the result of that invocation.
	 *
	 * @param other a {@code Supplier} whose result is returned if no mod object
	 *              is present
	 * @return the mod object if present otherwise the result of {@code other.get()}
	 * @throws NullPointerException if mod object is not present and {@code other} is
	 *                              null
	 */
	public T orElseGet(Supplier<? extends T> other) {
		return isPresent() ? get() : other.get();
	}

	/**
	 * Return the contained mod object, if present, otherwise throw an exception
	 * to be created by the provided supplier.
	 *
	 * @param <X>               Type of the exception to be thrown
	 * @param exceptionSupplier The supplier which will return the exception to
	 *                          be thrown
	 * @return the present mod object
	 * @throws X                    if there is no mod object present
	 * @throws NullPointerException if no mod object is present and
	 *                              {@code exceptionSupplier} is null
	 * @apiNote A method reference to the exception constructor with an empty
	 * argument list can be used as the supplier. For example,
	 * {@code IllegalStateException::new}
	 */
	public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
		if (get() != null) {
			return get();
		} else {
			throw exceptionSupplier.get();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof RegistryObject) {
			return Objects.equals(((RegistryObject<?>) obj).identifier, identifier);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(identifier);
	}
}
