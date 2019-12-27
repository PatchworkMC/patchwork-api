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
	 * @reason Patch this class to drop stacks for any shearable entity. An overwrite was required here as the mixin was
	 * too complicated to write without one.
	 * @author SuperCoder79
	 */
	@Overwrite
	public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
		World world = pointer.getWorld();
		if (!world.isClient()) {
			this.success = false;
			BlockPos pos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
			List<Entity> entities = world.getEntities(Entity.class, new Box(pos));

			for (Entity entity : entities) {
				if (entity instanceof IShearable) {
					IShearable target = (IShearable) entity;
					if (target.isShearable(stack, world, pos)) {
						List<ItemStack> drops = target.onSheared(stack, world, pos, EnchantmentHelper.getLevel(Enchantments.FORTUNE, stack));
						Random rand = new Random();
						for (ItemStack drop : drops) {
							ItemEntity ent = entity.dropStack(drop, 1.0F);
							ent.setVelocity(ent.getVelocity().add((rand.nextFloat() - rand.nextFloat()) * 0.1F, rand.nextFloat() * 0.05F, (rand.nextFloat() - rand.nextFloat()) * 0.1F));
						}
						this.success = true;
					}
				}
			}
		}

		return stack;
	}
}
