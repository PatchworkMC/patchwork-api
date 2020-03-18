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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.integrated.IntegratedServer;

import net.patchworkmc.impl.event.lifecycle.LifecycleEvents;

/**
 * Mixes into {@link IntegratedServer} and {@link MinecraftDedicatedServer} in order to implement
 * {@link net.minecraftforge.fml.event.server.FMLServerStartingEvent}. This event fires right before the implementations
 * return <code>true</code> from <code>setupServer</code>. Returning <code>false</code> from the callback  cancels the
 * server's startup, however, it's important to note that this event isn't actually cancellable in Forge!
 */
@Mixin({IntegratedServer.class, MinecraftDedicatedServer.class})
public class MixinMinecraftServerSubclass {
	@Inject(method = "setupServer", at = @At("RETURN"))
	private void hookSetupEnd(CallbackInfoReturnable<Boolean> callback) {
		LifecycleEvents.handleServerStarting((MinecraftServer) (Object) this);
	}
}
