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

import net.minecraftforge.eventbus.api.GenericEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.minecraft.util.Identifier;

public class RegistryEvent<T> extends GenericEvent<T> {
	RegistryEvent(Class<T> clazz) {
		super(clazz);
	}

	/**
	 * Register new registries when you receive this event.
	 */
	public static class NewRegistry extends net.minecraftforge.eventbus.api.Event {
		@Override
		public String toString() {
			return "RegistryEvent.NewRegistry";
		}
	}

	public static class Register<V extends IForgeRegistryEntry<V>> extends RegistryEvent<V> {
		private final IForgeRegistry<V> registry;
		private final Identifier name;

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

		@Override
		public String toString() {
			return "RegistryEvent.Register<" + registry.getRegistryName() + ">";
		}
	}
}
