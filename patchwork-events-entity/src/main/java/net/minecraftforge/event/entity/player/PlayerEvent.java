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

package net.minecraftforge.event.entity.player;

import javax.annotation.Nonnull;

import net.minecraftforge.event.entity.living.LivingEvent;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;

/**
 * PlayerEvent is fired whenever an event involving Living entities occurs.
 *
 * <p>If a method utilizes this {@link net.minecraftforge.eventbus.api.Event} as its parameter, the method will
 * receive every child event of this class.</p>
 *
 * <p>All children of this event are fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.</p>
 */
public class PlayerEvent extends LivingEvent {
	private final PlayerEntity playerEntity;

	public PlayerEvent(PlayerEntity player) {
		super(player);
		playerEntity = player;
	}

	/**
	 * Use {@link #getPlayer()}.
	 *
	 * @return Player
	 */
	@Deprecated
	public PlayerEntity getPlayerEntity() {
		return playerEntity;
	}

	/**
	 * @return Player
	 */
	public PlayerEntity getPlayer() {
		return playerEntity;
	}

	/**
	 * Fired when an Entity is started to be "tracked" by this player (the player receives updates about this entity, e.g. motion).
	 */
	public static class StartTracking extends PlayerEvent {
		private final Entity target;

		public StartTracking(PlayerEntity player, Entity target) {
			super(player);
			this.target = target;
		}

		public Entity getTarget() {
			return target;
		}
	}

	/**
	 * Fired when an Entity is started to be "tracked" by this player (the player receives updates about this entity, e.g. motion).
	 */
	public static class StopTracking extends PlayerEvent {
		private final Entity target;

		public StopTracking(PlayerEntity player, Entity target) {
			super(player);
			this.target = target;
		}

		public Entity getTarget() {
			return target;
		}
	}

	/*
	 * Fired when the EntityPlayer is cloned, typically caused by the network sending a RESPAWN_PLAYER event.
	 * Either caused by death, or by traveling from the End to the overworld.
	 */
	public static class Clone extends PlayerEvent {
		private final PlayerEntity original;
		private final boolean wasDeath;

		public Clone(PlayerEntity newPlayer, PlayerEntity oldPlayer, boolean wasDeath) {
			super(newPlayer);
			this.original = oldPlayer;
			this.wasDeath = wasDeath;
		}

		/**
		 * @return The old EntityPlayer that this new entity is a clone of.
		 */
		public PlayerEntity getOriginal() {
			return original;
		}

		/**
		 * True if this event was fired because the player died.
		 * False if it was fired because the entity switched dimensions.
		 *
		 * @return Whether this event was caused by the player dying.
		 */
		public boolean isWasDeath() {
			return wasDeath;
		}
	}

	public static class ItemPickupEvent extends PlayerEvent {
		/**
		 * Original EntityItem with current remaining stack size.
		 */
		private final ItemEntity originalEntity;
		/**
		 * Clone item stack, containing the item and amount picked up.
		 */
		private final ItemStack stack;

		public ItemPickupEvent(PlayerEntity player, ItemEntity entPickedUp, ItemStack stack) {
			super(player);
			this.originalEntity = entPickedUp;
			this.stack = stack;
		}

		public ItemStack getStack() {
			return stack;
		}

		public ItemEntity getOriginalEntity() {
			return originalEntity;
		}
	}

	public static class ItemCraftedEvent extends PlayerEvent {
		@Nonnull
		private final ItemStack crafting;
		private final Inventory craftMatrix;

		public ItemCraftedEvent(PlayerEntity player, @Nonnull ItemStack crafting, Inventory craftMatrix) {
			super(player);
			this.crafting = crafting;
			this.craftMatrix = craftMatrix;
		}

		@Nonnull
		public ItemStack getCrafting() {
			return this.crafting;
		}

		public Inventory getInventory() {
			return this.craftMatrix;
		}
	}

	public static class ItemSmeltedEvent extends PlayerEvent {
		@Nonnull
		private final ItemStack smelting;

		public ItemSmeltedEvent(PlayerEntity player, @Nonnull ItemStack crafting) {
			super(player);
			this.smelting = crafting;
		}

		@Nonnull
		public ItemStack getSmelting() {
			return this.smelting;
		}
	}

	/**
	 * Called on the server at the end of {@link net.minecraft.server.PlayerManager#onPlayerConnect(net.minecraft.network.ClientConnection, net.minecraft.server.network.ServerPlayerEntity)}
	 * when the player has finished logging in.
	 */
	public static class PlayerLoggedInEvent extends PlayerEvent {
		public PlayerLoggedInEvent(PlayerEntity player) {
			super(player);
		}
	}

	public static class PlayerLoggedOutEvent extends PlayerEvent {
		public PlayerLoggedOutEvent(PlayerEntity player) {
			super(player);
		}
	}

	public static class PlayerRespawnEvent extends PlayerEvent {
		private final boolean alive;

		public PlayerRespawnEvent(PlayerEntity player, boolean alive) {
			super(player);
			this.alive = alive;
		}

		/**
		 * Did this respawn event come from the player conquering the end?
		 * TODO: Forge should name this to isAlive.
		 * @return if this respawn was because the player conquered the end
		 */
		public boolean isEndConquered() {
			return this.alive;
		}
	}

	public static class PlayerChangedDimensionEvent extends PlayerEvent {
		private final DimensionType fromDim;
		private final DimensionType toDim;

		public PlayerChangedDimensionEvent(PlayerEntity player, DimensionType fromDim, DimensionType toDim) {
			super(player);
			this.fromDim = fromDim;
			this.toDim = toDim;
		}

		public DimensionType getFrom() {
			return this.fromDim;
		}

		public DimensionType getTo() {
			return this.toDim;
		}
	}

	/*TODO Events:
	HarvestCheck
	BreakSpeed
	NameFormat
	LoadFromFile
	SaveToFile
	Visibility called by ForgeHooks.getPlayerVisibilityDistance, but latter is not called elsewhere*/
}
