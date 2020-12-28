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

package net.patchworkmc.mixin.extensions.entity;

import java.util.Collection;

import javax.annotation.Nullable;

import net.minecraftforge.common.extensions.IForgeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;

@Mixin(Entity.class)
public class MixinEntity implements IForgeEntity {
	@Unique
	private Collection<ItemEntity> captureDrops = null;

	@Redirect(method = "dropStack(Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/ItemEntity;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
	private boolean hookDropStackForCapture(World world, Entity entity) {
		ItemEntity itemEntity = (ItemEntity) entity;

		if (captureDrops() != null) {
			captureDrops().add(itemEntity);
			return true;
		} else {
			return world.spawnEntity(itemEntity);
		}
	}

	@Nullable
	@Override
	public Collection<ItemEntity> captureDrops() {
		return captureDrops;
	}

	@Override
	public Collection<ItemEntity> captureDrops(@Nullable Collection<ItemEntity> value) {
		Collection<ItemEntity> ret = captureDrops;
		this.captureDrops = value;
		return ret;
	}
}
