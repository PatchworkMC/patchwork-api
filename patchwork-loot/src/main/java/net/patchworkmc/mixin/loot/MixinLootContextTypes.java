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

import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;

@Mixin(LootContextTypes.class)
public class MixinLootContextTypes {
	@Inject(method = "method_15970(Lnet/minecraft/loot/context/LootContextType$Builder;)V", at = @At("RETURN"))
	private static void patchwork_addChestParameters(LootContextType.Builder builder, CallbackInfo callback) {
		// Chest minecarts can have killers.
		builder.allow(LootContextParameters.KILLER_ENTITY);
	}

	@Inject(method = "method_764(Lnet/minecraft/loot/context/LootContextType$Builder;)V", at = @At("RETURN"))
	private static void patchwork_addFishingParameters(LootContextType.Builder builder, CallbackInfo callback) {
		// Entity that is using the fishing rod
		builder.allow(LootContextParameters.KILLER_ENTITY);

		// Fishing bobber entity
		builder.allow(LootContextParameters.THIS_ENTITY);
	}
}
