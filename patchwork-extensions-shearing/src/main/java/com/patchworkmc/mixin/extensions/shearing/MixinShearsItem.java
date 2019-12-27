package com.patchworkmc.mixin.extensions.shearing;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IShearable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.List;
import java.util.Random;

/**
 * Patch ShearsItem to allow it to shear any IShearable.
 *
 * @author SuperCoder79
 */
@Mixin(ShearsItem.class)
public class MixinShearsItem extends Item {

	public MixinShearsItem(Settings settings) {
		super(settings);
	}

	@Override
	public boolean useOnEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity entity, Hand hand) {
		if (entity.world.isClient) return false;

		if (entity instanceof IShearable) {
			IShearable target = (IShearable)entity;
			BlockPos pos = entity.getBlockPos();
			if (target.isShearable(stack, entity.world, pos)) {
				List<ItemStack> drops = target.onSheared(stack, entity.world, pos, EnchantmentHelper.getLevel(Enchantments.FORTUNE, stack));
				Random rand = new Random();
				drops.forEach(d -> {
					ItemEntity ent = entity.dropStack(d, 1.0F);
					ent.setVelocity(ent.getVelocity().add((rand.nextFloat() - rand.nextFloat()) * 0.1F, rand.nextFloat() * 0.05F, (rand.nextFloat() - rand.nextFloat()) * 0.1F));
				});

				stack.damage(1, entity, e -> e.sendToolBreakStatus(hand));
			}
			return true;
		}
		return false;
	}
}
