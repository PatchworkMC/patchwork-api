package net.patchworkmc.mixin.loot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(AdvancementRewards.class)
public class MixinAdvancementRewards {
	private static final String LOOT_CONTEXT_BUILD_TARGET =
			"net/minecraft/world/loot/context/LootContext$Builder.build(Lnet/minecraft/world/loot/context/LootContextType;)Lnet/minecraft/world/loot/context/LootContext;";

	@Inject(method = "apply(Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At(value = "INVOKE", target = LOOT_CONTEXT_BUILD_TARGET), locals = LocalCapture.CAPTURE_FAILHARD)
	private void patchwork_addLuckToLootContext(ServerPlayerEntity player, CallbackInfo callback) {

	}
}
