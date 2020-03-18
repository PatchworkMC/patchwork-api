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

package net.patchworkmc.mixin.networking;

import java.util.function.BiFunction;

import net.minecraftforge.fml.network.FMLPlayMessages;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

import net.patchworkmc.impl.networking.ClientEntitySpawner;

@Mixin(EntityType.class)
public class MixinEntityType<T extends Entity> implements ClientEntitySpawner<T> {
	@Unique
	private BiFunction<FMLPlayMessages.SpawnEntity, World, T> customClientFactory;

	@Override
	public T customClientSpawn(FMLPlayMessages.SpawnEntity packet, World world) {
		if (customClientFactory != null) {
			return customClientFactory.apply(packet, world);
		}

		EntityType<T> entityType = (EntityType<T>) (Object) this;

		return entityType.create(world);
	}

	@Override
	public void patchwork$setCustomClientFactory(BiFunction<FMLPlayMessages.SpawnEntity, World, T> customClientFactory) {
		this.customClientFactory = customClientFactory;
	}
}
