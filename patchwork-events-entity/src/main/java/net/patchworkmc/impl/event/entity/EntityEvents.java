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

package net.patchworkmc.impl.event.entity;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeItemStack;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.jetbrains.annotations.NotNull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class EntityEvents {
	public static EntityEvent.Size getEntitySizeForge(Entity player, EntityPose pose, EntityDimensions size, float eyeHeight) {
		EntityEvent.Size evt = new EntityEvent.Size(player, pose, size, eyeHeight);
		MinecraftForge.EVENT_BUS.post(evt);
		return evt;
	}

	public static int onItemExpire(ItemEntity entity, @NotNull ItemStack item) {
		if (item.isEmpty()) return -1;
		ItemExpireEvent event = new ItemExpireEvent(entity, ((IForgeItemStack) (Object) item).getEntityLifespan(entity.world));
		if (!MinecraftForge.EVENT_BUS.post(event)) return -1;
		return event.getExtraLife();
	}

	public static boolean onPlayerTossEvent(PlayerEntity player, ItemEntity itemEntity) {
		return MinecraftForge.EVENT_BUS.post(new ItemTossEvent(itemEntity, player));
	}

	public static boolean onLivingUpdateEvent(LivingEntity entity) {
		return MinecraftForge.EVENT_BUS.post(new LivingEvent.LivingUpdateEvent(entity));
	}
}
