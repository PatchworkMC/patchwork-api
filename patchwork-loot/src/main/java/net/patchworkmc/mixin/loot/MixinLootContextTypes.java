package net.patchworkmc.mixin.loot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextType;
import net.minecraft.world.loot.context.LootContextTypes;

@Mixin(LootContextTypes.class)
public class MixinLootContextTypes {
	@Inject(method = "method_15970(Lnet/minecraft/world/loot/context/LootContextType$Builder;)V", at = @At("RETURN"))
	private static void patchwork_addChestParameters(LootContextType.Builder builder, CallbackInfo callback) {
		// Chest minecarts can have killers.
		builder.allow(LootContextParameters.KILLER_ENTITY);
	}

	@Inject(method = "method_764(Lnet/minecraft/world/loot/context/LootContextType$Builder;)V", at = @At("RETURN"))
	private static void patchwork_addFishingParameters(LootContextType.Builder builder, CallbackInfo callback) {
		// Entity that is using the fishing rod
		builder.allow(LootContextParameters.KILLER_ENTITY);

		// Fishing bobber entity
		builder.allow(LootContextParameters.THIS_ENTITY);
	}
}
