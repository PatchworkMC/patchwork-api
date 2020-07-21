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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTableRange;
import net.minecraft.loot.UniformLootTableRange;

import net.patchworkmc.api.loot.ForgeLootPool;
import net.patchworkmc.impl.loot.PatchworkLootPool;

@Mixin(LootPool.class)
public class MixinLootPool implements PatchworkLootPool, ForgeLootPool {
	// Forge has this as final, but I don't have a good way to initialize it if it is final.
	@Unique
	private String name;

	@Shadow
	private UniformLootTableRange bonusRollsRange;

	@Shadow
	private LootTableRange rollsRange;

	// implementation detail
	// TODO: if we could have an inner class that was also a mixin, we could set this as protected?
	@Override
	public void patchwork$setName(String name) {
		this.name = name;
	}

	// Forge methods that should be added directly to the type

	// TODO: freezing stuff

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public LootTableRange getRolls() {
		return rollsRange;
	}

	@Override
	public LootTableRange getBonusRolls() {
		return this.bonusRollsRange;
	}

	@Override
	public void setRolls(UniformLootTableRange v) {
		// checkFrozen();
		this.rollsRange = v;
	}

	@Override
	public void setBonusRolls(UniformLootTableRange v) {
		// checkFrozen();
		this.bonusRollsRange = v;
	}
}
