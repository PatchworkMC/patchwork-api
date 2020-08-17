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

import java.util.Locale;

import com.google.common.collect.BiMap;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistryInternal;
import net.minecraftforge.registries.RegistryManager;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;

public class FeatureCallbacks<V extends Feature<?> & IForgeRegistryEntry<V>>
		implements IForgeRegistry.AddCallback<V>, IForgeRegistry.CreateCallback<V> {
	@SuppressWarnings("rawtypes")
	public static final FeatureCallbacks INSTANCE = new FeatureCallbacks();
	private static final Identifier STRUCTURE_FEATURES = new Identifier("minecraft:structure_feature");
	private static final Identifier STRUCTURES = new Identifier("minecraft:structures");

	private FeatureCallbacks() {
	}

	@Override
	public void onAdd(IForgeRegistryInternal<V> owner, RegistryManager stage, int id, V obj, V oldObj) {
		if (obj instanceof StructureFeature) {
			StructureFeature<?> structure = (StructureFeature<?>) obj;
			String key = structure.getName().toLowerCase(Locale.ROOT);

			@SuppressWarnings("unchecked")
			Registry<StructureFeature<?>> reg = owner.getSlaveMap(STRUCTURE_FEATURES, Registry.class);
			Registry.register(reg, key, structure);

			@SuppressWarnings("unchecked")
			BiMap<String, StructureFeature<?>> map = owner.getSlaveMap(STRUCTURES, BiMap.class);

			if (oldObj != null && oldObj instanceof StructureFeature) {
				map.remove(((StructureFeature<?>) oldObj).getName().toLowerCase(Locale.ROOT));
			}

			map.put(key, structure);
		}
	}

	@Override
	public void onCreate(IForgeRegistryInternal<V> owner, RegistryManager stage) {
		owner.setSlaveMap(STRUCTURE_FEATURES, Registry.REGISTRIES.get(STRUCTURE_FEATURES));
		owner.setSlaveMap(STRUCTURES, StructureFeature.STRUCTURES);
	}
}
