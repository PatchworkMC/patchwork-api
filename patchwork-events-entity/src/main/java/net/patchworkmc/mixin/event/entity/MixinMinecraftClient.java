package net.patchworkmc.mixin.event.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.patchworkmc.impl.event.entity.PlayerEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {
	@Shadow
	public ClientPlayerEntity player;

	@Inject(method = "doAttack()V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;resetLastAttackedTicks()V", shift = At.Shift.AFTER))
	private void fireLeftClickEmpty(CallbackInfo ci) {
		PlayerEvents.fireLeftClickEmptyEvent(player);
	}
}
