package com.patchworkmc.mixin.extensions.shearing;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Patches the inner class containing the logic for dispensers when using shears, to allow them to shear any
 * IShearable entity.
 *
 * @author SuperCoder79
 */
@Mixin(targets = "net/minecraft/block/dispenser/DispenserBehavior$13")
public class MixinDispenserBehavior {
	@Unique
	protected boolean success;
	/**
	 * @reason Patch this class to drop stacks for any shearable entity.
	 * @author SuperCoder79
	 */
	@Overwrite
	public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
		World world = pointer.getWorld();
		if (!world.isClient()) {
			this.success = false;
			BlockPos blockPos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
			List<Entity> list = world.getEntities(Entity.class, new Box(blockPos));

			for (Entity entity : list) {
				if (entity instanceof IShearable) {
					IShearable target = (IShearable) entity;
					if (target.isShearable(stack, world, blockPos)) {
						List<ItemStack> drops = target.onSheared(stack, world, blockPos, EnchantmentHelper.getLevel(Enchantments.FORTUNE, stack));
						Random rand = new Random();
						drops.forEach(d -> {
							ItemEntity ent = entity.dropStack(d, 1.0F);
							ent.setVelocity(ent.getVelocity().add((rand.nextFloat() - rand.nextFloat()) * 0.1F, rand.nextFloat() * 0.05F, (rand.nextFloat() - rand.nextFloat()) * 0.1F));
						});
						this.success = true;
					}
				}
			}
		}

		return stack;
	}
}
