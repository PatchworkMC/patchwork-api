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

package net.patchworkmc.impl.registries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@SuppressWarnings("rawtypes")
public class RegistryEventDispatcher {
	private static final boolean CHECK_SUPERS = false;

	/**
	 * @return the ordering of registries that Forge expects
	 */
	private static List<Identifier> getExpectedOrdering() {
		List<Identifier> registries = new ArrayList<>(RegistryManager.ACTIVE.getRegistryNames());

		registries.remove(Registry.BLOCK_KEY.getValue());
		registries.remove(Registry.ITEM_KEY.getValue());

		registries.sort((o1, o2) -> String.valueOf(o1).compareToIgnoreCase(String.valueOf(o2)));

		registries.add(0, Registry.BLOCK_KEY.getValue());
		registries.add(1, Registry.ITEM_KEY.getValue());

		return registries;
	}

	@SuppressWarnings("unchecked")
	public static void dispatchRegistryEvents(Consumer<RegistryEvent.Register> handler) {
		List<Identifier> expectedOrder = getExpectedOrdering();
		int registeredSize = RegistryManager.ACTIVE.getRegistryNames().size();

		if (registeredSize < expectedOrder.size()) {
			for (Identifier identifier : expectedOrder) {
				System.out.println("expected: " + identifier);
			}

			for (Identifier identifier : RegistryManager.ACTIVE.getRegistryNames()) {
				System.out.println("got: " + identifier);
			}

			throw new IllegalStateException("RegistryEventDispatcher is missing " + (expectedOrder.size() - registeredSize) + " registries!");
		}

		for (Identifier identifier : expectedOrder) {
			ForgeRegistry registry = RegistryManager.ACTIVE.getRegistry(identifier);

			if (CHECK_SUPERS) {
				Class superType = registry.getRegistrySuperType();

				for (Map.Entry<Identifier, Object> entry : (Set<Map.Entry<Identifier, Object>>) registry.getEntries()) {
					if (!superType.isAssignableFrom(entry.getValue().getClass())) {
						throw new IllegalStateException("Bad registry type for " + identifier + " (" + entry.getKey() + ")");
					}
				}
			}

			registry.unfreeze();
			handler.accept(new RegistryEvent.Register(registry));
			// If we freeze the registry here, Fabric mods loaded after us will not be able to register things.
			// TODO: Find a better location to freeze the registry.
			// registry.freeze();
		}
	}
}
