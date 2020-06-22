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

package net.patchworkmc.mixin.event.entity;

import java.util.UUID;

import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

import net.patchworkmc.impl.event.entity.EntityEvents;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity {
	@Shadow
	private int pickupDelay, age;

	@Shadow
	private UUID owner;

	@Shadow
	public abstract ItemStack getStack();

	protected MixinItemEntity(EntityType<? extends ItemEntity> entityType, World world) {
		super(entityType, world);
	}

	/**
	 * @author Rongmario
	 * @reason <p>To fully adapt {@link ItemEntity#onPlayerCollision} to Forge's patched version, approximately
	 * 3 injections and 2 redirects is needed. Which at that point, Overwrite might be a better option.</p>
	 *
	 * <p>This also eliminates the need of holding 2 references (that might have used ThreadLocal)
	 * for {@link EntityItemPickupEvent}'s result and a copied ItemStack.</p>
	 *
	 * <p>Small changes to Forge's implementation:
	 * 	  1. pickupDelay is only checked once.
	 * 	  2. Variables are assigned after the the first return.</p>
	 */
	@Overwrite
	public void onPlayerCollision(PlayerEntity player) {
		if (!this.world.isClient && this.pickupDelay <= 0) {
			final ItemEntity entity = (ItemEntity) (Object) this;
			int result = EntityEvents.onItemPickup(entity, player);

			if (result < 0) {
				return;
			}

			ItemStack itemStack = entity.getStack();
			int i = itemStack.getCount();
			ItemStack copy = itemStack.copy();

			// TODO: '6000' is hardcoded right now, but Forge has exposed it through IForgeItem#getEntityLifespan
			if ((this.owner == null || 6000 - this.age <= 200 || this.owner.equals(player.getUuid())) && (result == 1 || i <= 0 || player.inventory.insertStack(itemStack))) {
				copy.setCount(copy.getCount() - i);
				EntityEvents.onPlayerItemPickup(player, entity, copy);

				if (itemStack.isEmpty()) {
					player.sendPickup(entity, i);
					entity.remove();
					itemStack.setCount(i);
				}

				player.increaseStat(Stats.PICKED_UP.getOrCreateStat(itemStack.getItem()), i);
			}
		}
	}
}
