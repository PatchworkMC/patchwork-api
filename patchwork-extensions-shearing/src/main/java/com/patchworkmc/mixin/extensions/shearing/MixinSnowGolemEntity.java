package com.patchworkmc.mixin.extensions.shearing;

import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraftforge.common.IShearable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Extends SnowGolemEntity to make it shearable if it has it's pumpkin. On shearing, the pumpkin will be gone.
 *
 * @author SuperCoder79
 */
@Mixin(SnowGolemEntity.class)
public abstract class MixinSnowGolemEntity implements IShearable {
	@Shadow
	public abstract boolean hasPumpkin();

	@Shadow
	public abstract void setHasPumpkin(boolean hasPumpkin);

	@Override
	public boolean isShearable(ItemStack item, ViewableWorld world, BlockPos pos) {
		return this.hasPumpkin();
   }

	@Override
	public List<ItemStack> onSheared(ItemStack item, IWorld world, BlockPos pos, int fortune) {
		this.setHasPumpkin(false);
		return new ArrayList<>();
   }

   //TODO: fix this
	@Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
	protected void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<Boolean> info) {
		info.setReturnValue(false);
   }
}
