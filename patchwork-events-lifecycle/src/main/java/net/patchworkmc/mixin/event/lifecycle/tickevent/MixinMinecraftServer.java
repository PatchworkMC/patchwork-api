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

package net.patchworkmc.mixin.event.lifecycle.tickevent;

import java.util.Iterator;
import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import net.patchworkmc.impl.event.lifecycle.LifecycleEvents;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getMeasuringTimeNano()J",
			ordinal = 0, shift = At.Shift.AFTER))
	public void patchwork$onPreServerTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		LifecycleEvents.onPreServerTick();
	}

	@Inject(method = "tick", at = @At(value = "TAIL"))
	public void patchwork$onPostServerTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		LifecycleEvents.onPostServerTick();
	}

	// having an ordinal breaks this
	@Inject(method = "tickWorlds", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
			args = "ldc=tick"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void patchwork$onPreWorldTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci, Iterator<?> ignored, ServerWorld serverWorld) {
		LifecycleEvents.onPreWorldTick(serverWorld);
	}

	@Inject(method = "tickWorlds", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V", ordinal = 0),
			locals = LocalCapture.CAPTURE_FAILHARD)
	public void patchwork$onPostWorldTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci, Iterator<?> var2, ServerWorld serverWorld) {
		LifecycleEvents.onPostWorldTick(serverWorld);
	}
}
