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

package net.patchworkmc.mixin.event.lifecycle.fml;

import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;

@Mixin(IntegratedServer.class)
public class MixinIntegratedServer {
	@Inject(method = "setupServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;loadWorld()V", ordinal = 0),
			cancellable = true)
	private void patchwork$serverAboutToStart(CallbackInfoReturnable<Boolean> cir) {
		if (!ServerLifecycleHooks.handleServerAboutToStart((MinecraftServer) (Object) this)) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "setupServer", at = @At("TAIL"), cancellable = true)
	private void patchwork$serverStarting(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(ServerLifecycleHooks.handleServerStarting((MinecraftServer) (Object) this));
	}
}
