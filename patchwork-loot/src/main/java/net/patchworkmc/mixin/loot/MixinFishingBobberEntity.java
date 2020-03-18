package net.patchworkmc.mixin.loot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameters;

@Mixin(FishingBobberEntity.class)
public class MixinFishingBobberEntity {
	private static final String LOOT_CONTEXT_BUILD_TARGET =
			"net/minecraft/world/loot/context/LootContext$Builder.build(Lnet/minecraft/world/loot/context/LootContextType;)Lnet/minecraft/world/loot/context/LootContext;";

	@Shadow
	private final PlayerEntity owner = null;

	@Inject(method = "method_6957(Lnet/minecraft/item/ItemStack;)I", at = @At(value = "INVOKE", target = LOOT_CONTEXT_BUILD_TARGET), locals = LocalCapture.CAPTURE_FAILHARD)
	private void patchwork_addFishingParameters(ItemStack stack, CallbackInfoReturnable<Integer> callback, int rodDamage, LootContext.Builder builder, LootSupplier supplier) {
		builder.put(LootContextParameters.KILLER_ENTITY, this.owner);
		builder.put(LootContextParameters.THIS_ENTITY, (Entity) (Object) this);
	}
}
