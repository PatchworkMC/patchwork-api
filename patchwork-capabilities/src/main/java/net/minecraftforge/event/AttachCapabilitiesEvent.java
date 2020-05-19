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

package net.minecraftforge.event;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.eventbus.api.GenericEvent;

import net.minecraft.util.Identifier;

/**
 * Fired whenever an object with Capabilities support (currently {@link net.minecraft.block.entity.BlockEntity block entities}, {@link net.minecraft.item.ItemStack items}, {@link net.minecraft.entity.Entity entities}, {@link net.minecraft.world.World worlds} and {@link net.minecraft.world.chunk.WorldChunk chunks})
 * is created. Allowing for the attachment of arbitrary capability providers.
 *
 * <p>Please note that as this is fired for ALL object creations efficient code is recommended.
 * And if possible use one of the sub-classes to filter your intended objects.
 */
public class AttachCapabilitiesEvent<T> extends GenericEvent<T> {
	private final T object;
	private final Map<Identifier, ICapabilityProvider> capabilities = Maps.newLinkedHashMap();
	private final Map<Identifier, ICapabilityProvider> view = Collections.unmodifiableMap(capabilities);
	private final List<Runnable> listeners = Lists.newArrayList();
	private final List<Runnable> listenersView = Collections.unmodifiableList(listeners);

	public AttachCapabilitiesEvent(Class<T> type, T object) {
		super(type);
		this.object = object;
	}

	/**
	 * Retrieves the object that is being created.
	 *
	 * <p><b>Note:</b> Object creation is still incomplete at this point
	 */
	public T getObject() {
		return this.object;
	}

	/**
	 * Adds a {@link net.minecraftforge.common.capabilities.ICapabilityProvider capability provider} to be attached to this object.
	 * Keys MUST be unique, it is suggested that you set the domain to your mod ID.
	 * If the capability is an instance of {@link net.minecraftforge.common.util.INBTSerializable}, this key will be used when serializing this capability.
	 *
	 * @param key The name of owner of this capability provider.
	 * @param cap The {@link ICapabilityProvider capability provider}
	 */
	public void addCapability(Identifier key, ICapabilityProvider cap) {
		if (capabilities.containsKey(key)) {
			throw new IllegalStateException("Duplicate Capability Key: " + key + " " + cap);
		}

		this.capabilities.put(key, cap);
	}

	/**
	 * @return An unmodifiable view of the capabilities that will be attached to this object.
	 */
	public Map<Identifier, ICapabilityProvider> getCapabilities() {
		return view;
	}

	/**
	 * Adds a callback that is fired when the attached object is invalidated.
	 * Such as an {@link net.minecraft.entity.Entity} or a {@link net.minecraft.block.entity.BlockEntity} being removed from world.
	 * All attached providers should invalidate all of their held capability instances.
	 */
	public void addListener(Runnable listener) {
		this.listeners.add(listener);
	}

	public List<Runnable> getListeners() {
		return this.listenersView;
	}
}
