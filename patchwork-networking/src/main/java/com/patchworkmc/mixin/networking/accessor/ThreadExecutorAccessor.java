package com.patchworkmc.mixin.networking.accessor;

import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.util.ThreadExecutor;

@Mixin(ThreadExecutor.class)
public interface ThreadExecutorAccessor {
	@Invoker("executeFuture")
	public CompletableFuture<Void> patchwork$executeFuture(Runnable runnable);
}
