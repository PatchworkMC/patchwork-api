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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
	@Unique
	private boolean serverDiedFromThrowable = false;

	@Inject(method = "runServer", at = @At(value = "INVOKE", target = "net/minecraft/util/Util.getMeasuringTimeMs()J",
			ordinal = 0))
	public void patchwork$handleServerStarted(CallbackInfo ci) {
		ServerLifecycleHooks.handleServerStarted((MinecraftServer) (Object) this);
	}

	// The only place for us to handle server stopping is in the finally block,
	// but we don't want to fire if this is happening after the catch block...
	// so we set a flag if we've entered the catch block, and check at the beginning of the finally block.
	// Alternatively we could just run the while loop's check, but we have to wiggle into an odd injection point so this
	// ends up being cleaner.
	@Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;Ljava/lang/Throwable;)V",
			ordinal = 0))
	public void patchwork$setDiedFromThrowableFlag(CallbackInfo ci) {
		this.serverDiedFromThrowable = true;
	}

	@Inject(method = "runServer", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;stopped:Z", ordinal = 0))
	public void patchwork$handleServerStopping(CallbackInfo ci) {
		if (serverDiedFromThrowable) {
			return; // see above
		}

		ServerLifecycleHooks.handleServerStopping((MinecraftServer) (Object) this);
		ServerLifecycleHooks.expectServerStopped(); // forge: has to come before setCrashReport() to avoid race conditions
	}

	// No ordinal because both of the times we need to set this happen to come before the only two setCrashReport calls
	@Inject(method = "runServer", at = @At(value = "INVOKE", target = "net/minecraft/server/MinecraftServer.setCrashReport(Lnet/minecraft/util/crash/CrashReport;)V"))
	public void patchwork$expectServerStopped(CallbackInfo ci) {
		ServerLifecycleHooks.expectServerStopped();
	}

	@Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;exit()V", ordinal = 0))
	public void patchwork$handleServerStopped(CallbackInfo ci) {
		ServerLifecycleHooks.handleServerStopped((MinecraftServer) (Object) this);
	}
}
