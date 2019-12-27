package com.patchworkmc.mixin.extensions.shearing;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Patches SheepEntity to allow shearing, dropping wool. This patch cancels the vanilla shearing code.
 *
 * @author SuperCoder79
 */
@Mixin(SheepEntity.class)
public abstract class MixinSheepEntity extends AnimalEntity implements IShearable {

	@Shadow
	public abstract DyeColor getColor();

	@Shadow
	@Final
	private static Map<DyeColor, ItemConvertible> DROPS;

	@Shadow
	public abstract boolean isSheared();

	@Shadow
	public abstract void setSheared(boolean bl);

	protected MixinSheepEntity(EntityType<? extends AnimalEntity> type, World world) {
		super(type, world);
	}

	@Override
	public boolean isShearable(ItemStack item, ViewableWorld world, BlockPos pos) {
	    return !this.isSheared() && !this.isBaby();
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, net.minecraft.world.IWorld world, BlockPos pos, int fortune) {
		List<ItemStack> ret = new java.util.ArrayList<>();
		if (!this.world.isClient) {
			this.setSheared(true);
			int i = 1 + this.random.nextInt(3);

			for(int j = 0; j < i; ++j) {
				ret.add(new ItemStack(DROPS.get(this.getColor())));
			}
		}

		this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);
		return ret;
	}

	@Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
	protected void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<Boolean> info) {
		info.setReturnValue(false);
	}
}
