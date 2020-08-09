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

package net.patchworkmc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.dedicated.MinecraftDedicatedServer;

import net.patchworkmc.impl.Patchwork;

@Mixin(MinecraftDedicatedServer.class)
public abstract class MixinMinecraftDedicatedServer {
	@Inject(method = "setupServer", at = @At(value = "INVOKE", shift = Shift.BEFORE, ordinal = 0, target = "org/apache/logging/log4j/Logger.info(Ljava/lang/String;)V"))
	private void initForgeModsOnServer(CallbackInfoReturnable<Boolean> ci) {
		Patchwork.beginServerModLoading();
	}

	@Inject(method = "setupServer", at = @At(value = "INVOKE", shift = Shift.BEFORE, ordinal = 0, target =
			"net/minecraft/server/MinecraftServer.setPlayerManager(Lnet/minecraft/server/PlayerManager;)V"))
	private void endOfModLoading(CallbackInfoReturnable<Boolean> ci) {
		Patchwork.endOfServerModLoading();
	}
}
