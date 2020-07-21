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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.UniformLootTableRange;

import net.patchworkmc.api.loot.ForgeLootPool;
import net.patchworkmc.impl.loot.PatchworkLootPool;

@Mixin(LootPool.Builder.class)
public abstract class MixinLootPoolBuilder implements ForgeLootPool.Builder {
	@Unique
	private String name;

	@Shadow
	private UniformLootTableRange bonusRollsRange;

	@Inject(method = "build", at = @At("RETURN"), cancellable = true)
	private void addNameToConstructor(CallbackInfoReturnable<LootPool> cir) {
		LootPool ret = cir.getReturnValue();
		((PatchworkLootPool) ret).patchwork$setName(name);

		// is this necessary?
		cir.setReturnValue(ret);
	}

	public LootPool.Builder name(String name) {
		this.name = name;
		return (LootPool.Builder) (Object) this;
	}

	public LootPool.Builder bonusRolls(float min, float max) {
		this.bonusRollsRange = new UniformLootTableRange(min, max);
		return (LootPool.Builder) (Object) this;
	}
}
