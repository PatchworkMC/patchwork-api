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

package net.patchworkmc.mixin.event.lifecycle;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.objectweb.asm.Opcodes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

import net.minecraft.client.MinecraftClient;

import net.patchworkmc.impl.event.lifecycle.LifecycleEvents;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
	// If this used HEAD, it doesn't run at exactly the same time as in Forge - itemUseCooldown
	// is decremented before the event fires there.
	// The first thing after that if statement is to start the GUI profiler. Hook before the field
	// fetch, and thus they always run before anything further down.
	@Inject(method = "tick()V", at = @At(value = "FIELD", opcode = Opcodes.H_GETFIELD, ordinal = 0,
					target = "Lnet/minecraft/client/MinecraftClient;profiler:Lnet/minecraft/util/profiler/DisableableProfiler;"))
	private void hookClientTickStart(CallbackInfo info) {
		TickEvent.ClientTickEvent event = new TickEvent.ClientTickEvent(TickEvent.Phase.START);
		MinecraftForge.EVENT_BUS.post(event);
	}

	@Inject(method = "tick()V", at = @At("RETURN"))
	private void hookClientTickEnd(CallbackInfo info) {
		TickEvent.ClientTickEvent event = new TickEvent.ClientTickEvent(TickEvent.Phase.END);
		MinecraftForge.EVENT_BUS.post(event);
	}

	@Inject(method = "init", at = @At("RETURN"))
	private void hookClientInit(CallbackInfo ci) {
		LifecycleEvents.onClientInitialized();
	}
}
