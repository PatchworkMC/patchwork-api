package net.patchworkmc.impl.event.entity;

import javax.annotation.Nonnull;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeItemStack;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

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

	public static int onItemExpire(ItemEntity entity, @Nonnull ItemStack item) {
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
