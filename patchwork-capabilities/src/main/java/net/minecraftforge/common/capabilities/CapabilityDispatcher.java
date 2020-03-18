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

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Lists;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.logging.log4j.LogManager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

/**
 * A high-speed implementation of a {@link Capability} delegator.
 * This is used to wrap the results of the {@link net.minecraftforge.event.AttachCapabilitiesEvent}.
 *
 * <p>It is HIGHLY recommended that you DO NOT use this approach unless
 * you MUST delegate to multiple providers instead just implementing
 * your handlers using normal if statements.
 *
 * <p>Internally the handlers are baked into arrays for fast iteration.
 * The {@link Identifier} will be used for the NBT Key when serializing.
 */
@ParametersAreNonnullByDefault
public final class CapabilityDispatcher implements INBTSerializable<CompoundTag>, ICapabilityProvider {
	private final ICapabilityProvider[] providers;
	private final INBTSerializable<Tag>[] writers;
	private final String[] names;
	private final List<Runnable> listeners;

	public CapabilityDispatcher(Map<Identifier, ICapabilityProvider> capabilities, List<Runnable> listeners) {
		this(capabilities, listeners, null);
	}

	@SuppressWarnings("unchecked")
	public CapabilityDispatcher(Map<Identifier, ICapabilityProvider> capabilities, List<Runnable> listeners, @Nullable ICapabilityProvider parent) {
		List<ICapabilityProvider> providers = Lists.newArrayList();
		List<INBTSerializable<Tag>> writers = Lists.newArrayList();
		List<String> names = Lists.newArrayList();

		// Parents go first!
		if (parent != null) {
			providers.add(parent);

			if (parent instanceof INBTSerializable) {
				writers.add((INBTSerializable<Tag>) parent);
				names.add("Parent");
			}
		}

		for (Map.Entry<Identifier, ICapabilityProvider> entry : capabilities.entrySet()) {
			ICapabilityProvider provider = entry.getValue();
			providers.add(provider);

			if (provider instanceof INBTSerializable) {
				writers.add((INBTSerializable<Tag>) provider);
				names.add(entry.getKey().toString());
			}
		}

		this.listeners = listeners;
		this.providers = providers.toArray(new ICapabilityProvider[0]);
		this.writers = writers.toArray(new INBTSerializable[0]);
		this.names = names.toArray(new String[0]);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		for (ICapabilityProvider provider : providers) {
			LazyOptional<T> ret = provider.getCapability(cap, side);

			//noinspection ConstantConditions
			if (ret == null) {
				throw new RuntimeException(
						String.format(
								"Provider %s.getCapability() returned null; return LazyOptional.empty() instead!",
								provider.getClass().getTypeName()
						)
				);
			}

			if (ret.isPresent()) {
				return ret;
			}
		}

		return LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();

		for (int x = 0; x < writers.length; x++) {
			try {
				tag.put(names[x], writers[x].serializeNBT());
			} catch (Exception exception) {
				LogManager.getLogger().error("A capability provider with the name " + names[x] + " has thrown an exception trying to write state. It will not persist. Report this to the mod author", exception);
			}
		}

		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		for (int x = 0; x < writers.length; x++) {
			if (tag.contains(names[x])) {
				try {
					writers[x].deserializeNBT(tag.get(names[x]));
				} catch (Exception exception) {
					LogManager.getLogger().error("A capability provider with the name " + names[x] + " has thrown an exception trying to read state. It will not persist. Report this to the mod author", exception);
				}
			}
		}
	}

	// Called from ItemStack to compare equality.
	// Only compares serializable caps.
	public boolean areCompatible(@Nullable CapabilityDispatcher other) {
		// Do some checks before costly NBT serialization and comparisons
		if (other == null) {
			return this.writers.length == 0;
		}

		if (this.writers.length == 0) {
			return other.writers.length == 0;
		}

		return this.serializeNBT().equals(other.serializeNBT());
	}

	public void invalidate() {
		this.listeners.forEach(Runnable::run);
	}
}
