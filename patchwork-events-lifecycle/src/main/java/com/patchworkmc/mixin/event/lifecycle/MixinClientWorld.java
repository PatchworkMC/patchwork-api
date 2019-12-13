package com.patchworkmc.mixin.event.lifecycle;

import java.util.function.BooleanSupplier;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.patchworkmc.impl.event.lifecycle.LifecycleEvents;

@Mixin(ClientWorld.class)
public class MixinClientWorld {
	@Inject(method = "tick", at = @At("HEAD"))
	private void tick(BooleanSupplier supplier, CallbackInfo callback) {
		LifecycleEvents.fireWorldTickEvent(TickEvent.Phase.START, (World)(Object)this);
	}
}
