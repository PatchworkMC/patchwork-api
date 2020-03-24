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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(AdvancementRewards.class)
public class MixinAdvancementRewards {
	private static final String LOOT_CONTEXT_BUILD_TARGET =
			"Lnet/minecraft/loot/context/LootContext$Builder;build(Lnet/minecraft/loot/context/LootContextType;)Lnet/minecraft/loot/context/LootContext;";

	@Inject(method = "apply(Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At(value = "INVOKE", target = LOOT_CONTEXT_BUILD_TARGET))
	private void patchwork_addLuckToLootContext(ServerPlayerEntity player, CallbackInfo callback) {
		// TODO: Add luck to the loot context, figure out locals
	}
}
