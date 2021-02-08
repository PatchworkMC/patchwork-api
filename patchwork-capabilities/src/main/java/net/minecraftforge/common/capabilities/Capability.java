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

package net.minecraftforge.common.capabilities;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Throwables;
import net.minecraftforge.common.util.LazyOptional;

import net.minecraft.nbt.Tag;
import net.minecraft.util.math.Direction;

/**
 * Capabilities are an extensible way of attaching arbitrary interfaces to classes that support
 * {@link ICapabilityProvider} using {@link ICapabilityProvider#getCapability}. This class is a marker that
 * represents support for the interface held in <code>&lt;T&gt;</code>
 *
 * <p>This class, beyond serving as a marker, also allows creation of the default implementation of the interface as well
 * as the ability to read and write the interface to NBT
 *
 * <p>When {@link CapabilityManager#register} is called, {@link CapabilityManager} creates a single instance of this class,
 * which is then passed to any listeners of {@link net.patchworkmc.api.capability.CapabilityRegisteredCallback}.
 *
 * <p>After registration, instances of this class may be used with {@link ICapabilityProvider#getCapability} and the
 * functions within {@link IStorage}
 *
 * <p>Each capability will have ONE instance of this class, and it will the the one passed into
 * {@link ICapabilityProvider}'s methods
 *
 * <p>The {@link CapabilityManager} is in charge of creating this class.
 *
 * @param <T> The data which is to be attached
 */
public class Capability<T> {
	public interface IStorage<T> {
		/**
		 * Serialize the capability instance to a NBTTag.
		 * This allows for a central implementation of saving the data.
		 * <p>
		 * It is important to note that it is up to the API defining
		 * the capability what requirements the 'instance' value must have.
		 * <p>
		 * Due to the possibility of manipulating internal data, some
		 * implementations MAY require that the 'instance' be an instance
		 * of the 'default' implementation.
		 * <p>
		 * Review the API docs for more info.
		 *
		 * @param capability The Capability being stored.
		 * @param instance   An instance of that capabilities interface.
		 * @param side       The side of the object the instance is associated with.
		 * @return a NBT holding the data. Null if no data needs to be stored.
		 */
		@Nullable
		Tag writeNBT(Capability<T> capability, T instance, Direction side);

		/**
		 * Read the capability instance from a NBT tag.
		 * <p>
		 * This allows for a central implementation of saving the data.
		 * <p>
		 * It is important to note that it is up to the API defining
		 * the capability what requirements the 'instance' value must have.
		 * <p>
		 * Due to the possibility of manipulating internal data, some
		 * implementations MAY require that the 'instance' be an instance
		 * of the 'default' implementation.
		 * <p>
		 * Review the API docs for more info.         *
		 *
		 * @param capability The Capability being stored.
		 * @param instance   An instance of that capabilities interface.
		 * @param side       The side of the object the instance is associated with.
		 * @param nbt        A NBT holding the data. Must not be null, as doesn't make sense to call this function with nothing to read...
		 */
		void readNBT(Capability<T> capability, T instance, Direction side, Tag nbt);
	}

	/**
	 * @return The unique name of this capability, typically this is
	 * the fully qualified class name for the target interface.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return An instance of the default storage handler. You can safely use this store your default implementation in NBT.
	 */
	public IStorage<T> getStorage() {
		return storage;
	}

	/**
	 * Quick access to the IStorage's readNBT.
	 * See {@link IStorage#readNBT(Capability, Object, EnumFacing, NBTBase)}  for documentation.
	 */
	public void readNBT(T instance, Direction side, Tag nbt) {
		storage.readNBT(this, instance, side, nbt);
	}

	/**
	 * Quick access to the IStorage's writeNBT.
	 * See {@link IStorage#writeNBT(Capability, Object, EnumFacing)} for documentation.
	 */
	@Nullable
	public Tag writeNBT(T instance, Direction side) {
		return storage.writeNBT(this, instance, side);
	}

	/**
	 * A NEW instance of the default implementation.
	 * <p>
	 * If it important to note that if you want to use the default storage
	 * you may be required to use this exact implementation.
	 * Refer to the owning API of the Capability in question.
	 *
	 * @return A NEW instance of the default implementation.
	 */
	@Nullable
	public T getDefaultInstance() {
		try {
			return this.factory.call();
		} catch (Exception e) {
			Throwables.throwIfUnchecked(e);
			throw new RuntimeException(e);
		}
	}

	public @Nonnull <R> LazyOptional<R> orEmpty(Capability<R> toCheck, LazyOptional<T> inst) {
		return this == toCheck ? inst.cast() : LazyOptional.empty();
	}

	// INTERNAL
	private final String name;
	private final IStorage<T> storage;
	private final Callable<? extends T> factory;

	Capability(String name, IStorage<T> storage, Callable<? extends T> factory) {
		this.name = name;
		this.storage = storage;
		this.factory = factory;
	}
}
