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

package net.patchworkmc.mixin.loot;

import java.util.Arrays;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;

import net.fabricmc.fabric.api.loot.v1.FabricLootSupplier;

import net.patchworkmc.api.loot.ForgeLootTable;
import net.patchworkmc.api.loot.ForgeLootPool;

@Mixin(LootTable.class)
public class MixinLootTable implements ForgeLootTable {
	@Shadow
	LootPool[] pools;

	// Forge added methods

	// TODO: freezing stuff

	@Override
	public LootPool getPool(String name) {
		return ((FabricLootSupplier) this).getPools().stream().filter(e -> name.equals(((ForgeLootPool) e).getName())).findFirst().orElse(null);
	}

	@Override
	public LootPool removePool(String name) {
		// checkFrozen();
		for (int idx = 0; idx < pools.length; ++idx) {
			LootPool pool = pools[idx];

			if (name.equals(((ForgeLootPool) pool).getName())) {
				// https://stackoverflow.com/a/644764
				System.arraycopy(pools, idx + 1, pools, idx, pools.length - 1 - idx);
				return pool;
			}
		}

		return null;
	}

	@Override
	public void addPool(LootPool pool) {
		// checkFrozen();
		if (((FabricLootSupplier) this).getPools().stream().anyMatch(e -> e == pool || ((ForgeLootPool) e).getName().equals(((ForgeLootPool) pool).getName()))) {
			throw new RuntimeException("Attempted to add a duplicate pool to loot table: " + ((ForgeLootPool) pool).getName());
		}

		pools = Arrays.copyOf(pools, pools.length + 1);
		pools[pools.length - 1] = pool;
	}
}
