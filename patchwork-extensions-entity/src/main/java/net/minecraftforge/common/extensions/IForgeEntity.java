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

package net.minecraftforge.common.extensions;

import java.util.Collection;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;

import net.patchworkmc.impl.extensions.entity.PatchworkEntityItems;

public interface IForgeEntity {
	default Entity getEntity() {
		return (Entity) this;
	}

	boolean canUpdate();

	void canUpdate(boolean value);

	@Nullable
	Collection<ItemEntity> captureDrops();

	Collection<ItemEntity> captureDrops(@Nullable Collection<ItemEntity> captureDrops);

	/**
	 * Used in model rendering to determine if the entity riding this entity should be in the 'sitting' position.
	 *
	 * @return false to prevent riders from displaying the 'sitting' animation.
	 */
	default boolean shouldRiderSit() {
		return true;
	}

	/**
	 * Called when a user uses the creative pick block button on this entity.
	 *
	 * @param target The full target the player is looking at
	 * @return A ItemStack to add to the player's inventory, empty ItemStack if nothing should be added.
	 */
	default ItemStack getPickedResult(HitResult target) {
		return PatchworkEntityItems.getPickedItem((Entity) this);
	}

	/**
	 * If a rider of this entity can interact with this entity. Should return true on the
	 * ridden entity if so.
	 *
	 * @return if the entity can be interacted with from a rider
	 */
	default boolean canRiderInteract() {
		return false;
	}

	/**
	 * Checks if this entity can continue to be ridden while it is underwater.
	 *
	 * <p>As a default implementation, this uses the rider-nonspecific {@link Entity#canBeRiddenInWater()} in order to
	 * preserve implementations of this method by fabric mods.</p>
	 *
	 * @param rider The entity that is riding this entity
	 * @return {@code true} if the entity can continue to ride underwater. {@code false} otherwise.
	 */
	default boolean canBeRiddenInWater(Entity rider) {
		return ((Entity) this).canBeRiddenInWater();
	}

	/**
	 * TODO: This must be reconciled with patchwork-extension-block's BlockHarvestManager#onFarmlandTrample when it is ported
	 * Checks if this {@link Entity} can trample a {@link Block}.
	 *
	 * @param pos The block pos
	 * @param fallDistance The fall distance
	 * @return {@code true} if this entity can trample, {@code false} otherwise
	 *
	boolean canTrample(BlockState state, BlockPos pos, float fallDistance); */

	/**
	 * Returns The classification of this entity
	 *
	 * @param forSpawnCount If this is being invoked to check spawn count caps.
	 * @return If the creature is of the type provided
	 */
	default SpawnGroup getClassification(boolean forSpawnCount) {
		return getEntity().getType().getSpawnGroup();
	}

	/**
	 * Gets whether this entity has been added to a world (for tracking). Specifically
	 * between the times when an entity is added to a world and the entity being removed
	 * from the world's tracked lists.
	 *
	 * <p>TODO: Forge uses isAddedToWorld for bugfixes related to chunk loading in Entity, ShulkerEntity,
	 * and LeashKnotEntity, which can be implemented in the future. </p>
	 *
	 * @return True if this entity is being tracked by a world
	 */
	boolean isAddedToWorld();

	/**
	 * Called after the entity has been added to the world's ticking list.
	 *
	 * <p>Can be overriden, but needs to call super to prevent MC-136995.</p>
	 */
	void onAddedToWorld();

	/**
	 * Called after the entity has been removed to the world's ticking list.
	 *
	 * <p>Can be overriden, but needs to call super to prevent MC-136995.</p>
	 */
	void onRemovedFromWorld();

	/**
	 * Revives an entity that has been removed from a world.
	 *
	 * <p>Used as replacement for entity.removed = true. Having it as a function allows the entity to react to being revived.</p>
	 */
	void revive();
}
