package com.patchworkmc.mixin.extensions.shearing;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShearsItem;

@Mixin(DispenserBlock.class)
public class MixinDispenserBlock {
	private static final String GET_BEHAVIOR_FOR_ITEM = "getBehaviorForItem(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/block/dispenser/DispenserBehavior;";

	@Shadow
	@Final
	private static Map<Item, DispenserBehavior> BEHAVIORS;

	@Inject(method = GET_BEHAVIOR_FOR_ITEM, at = @At("RETURN"), cancellable = true)
	private void allowModdedShears(ItemStack stack, CallbackInfoReturnable<DispenserBehavior> callback) {
		Item item = stack.getItem();

		if(item != Items.SHEARS && item instanceof ShearsItem) {
			callback.setReturnValue(BEHAVIORS.get(Items.SHEARS));
		}
	}
}
