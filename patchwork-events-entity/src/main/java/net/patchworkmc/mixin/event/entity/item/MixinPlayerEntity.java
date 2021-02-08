package net.patchworkmc.mixin.event.entity.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import net.patchworkmc.impl.event.entity.EntityEvents;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {
	@Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;",
			at = @At(value = "RETURN", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void onDropItem(ItemStack itemStack, boolean bl, boolean bl2, CallbackInfoReturnable<ItemEntity> ci, double y, ItemEntity itemEntity) {
		// Note: This is implemented slightly differently from forge, since forge only calls this event on
		// dropSelectedItem(boolean) and dropItem(ItemStack, boolean), but this way makes it much nicer to implement
		// and should produce the same behavior for modders

		if (EntityEvents.onPlayerTossEvent((PlayerEntity) (Object) this, itemEntity)) {
			ci.setReturnValue(null);
		}
	}
}
