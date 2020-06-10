package net.patchworkmc.mixin.extension;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraftforge.common.util.FakePlayer;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(PlayerAdvancementTracker.class)
public class MixinPlayerAdvancementTracker {
	@Shadow
	private ServerPlayerEntity owner;

	@Inject(method = "grantCriterion", at = @At("HEAD"), cancellable = true)
	private void onGrantCriterion(Advancement advancement, String criterion, CallbackInfoReturnable<Boolean> cir) {
		if (owner instanceof FakePlayer) {
			cir.setReturnValue(false);
		}
	}
}
