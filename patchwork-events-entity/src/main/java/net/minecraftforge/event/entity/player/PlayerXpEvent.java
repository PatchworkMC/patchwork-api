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

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;

public abstract class PlayerXpEvent extends PlayerEvent {
	public PlayerXpEvent(PlayerEntity player) {
		super(player);
	}

	// For legacy reasons, the instances of this class should actually be of
	// type PlayerPickupXpEvent
	public static class PickupXp extends CancelablePlayerXpEvent {
		private final ExperienceOrbEntity orb;

		public PickupXp(PlayerEntity player, ExperienceOrbEntity orb) {
			super(player);
			this.orb = orb;
		}

		public ExperienceOrbEntity getOrb() {
			return orb;
		}
	}

	public static class XpChange extends CancelablePlayerXpEvent {
		private int amount;

		public XpChange(PlayerEntity player, int amount) {
			super(player);
			this.amount = amount;
		}

		public int getAmount() {
			return amount;
		}

		public void setAmount(int amount) {
			this.amount = amount;
		}
	}

	public static class LevelChange extends CancelablePlayerXpEvent {
		private int levels;

		public LevelChange(PlayerEntity player, int levels) {
			super(player);
			this.levels = levels;
		}

		public int getLevels() {
			return this.levels;
		}

		public void setLevels(int levels) {
			this.levels = levels;
		}
	}

	// Helper, so we don't have to repeat isCancelable all the time
	private static class CancelablePlayerXpEvent extends PlayerXpEvent {
		private CancelablePlayerXpEvent(PlayerEntity player) {
			super(player);
		}

		@Override
		public boolean isCancelable() {
			return true;
		}
	}
}
