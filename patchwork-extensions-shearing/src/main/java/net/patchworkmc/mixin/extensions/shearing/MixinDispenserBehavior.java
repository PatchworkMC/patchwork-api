/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2020, 2019-2020
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.patchworkmc.mixin.extensions.shearing;

import java.util.List;

import net.minecraftforge.common.IShearable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import net.patchworkmc.impl.extensions.shearing.Shearables;

/**
 * Patches the inner class containing the logic for dispensers when using shears, to allow them to shear any
 * {@link IShearable} entity.
 *
 * @author SuperCoder79
 */
@Mixin(targets = "net/minecraft/block/dispenser/DispenserBehavior$13")
public class MixinDispenserBehavior extends FallibleItemDispenserBehavior {
	private static final String DISPENSE_SILENTLY = "net/minecraft/block/dispenser/DispenserBehavior$13.dispenseSilently(Lnet/minecraft/util/math/BlockPointer;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;";

	/**
	 * Shears non-sheep entities that implement {@link IShearable}. The vanilla code will handle sheep entities.
	 *
	 * @author SuperCoder79
	 */
	@Inject(method = DISPENSE_SILENTLY, at = @At("RETURN"))
	private void onDispenseSilently(BlockPointer pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> callback) {
		World world = pointer.getWorld();

		if (!world.isClient()) {
			BlockPos pos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
			List<Entity> entities = world.getEntitiesByClass(Entity.class, new Box(pos),
					entity -> !entity.isSpectator() && entity instanceof IShearable && entity.getType() != EntityType.SHEEP
			);

			for (Entity entity : entities) {
				IShearable target = (IShearable) entity;

				if (!target.isShearable(stack, world, pos)) {
					continue;
				}

				Shearables.shearEntity(stack, world, pos, target);

				this.success = true;
			}
		}
	}

	@Inject(method = DISPENSE_SILENTLY, at = @At(value = "INVOKE", target = "net/minecraft/entity/passive/SheepEntity.dropItems()V"))
	private void assertThatThisIsTheShearingDispenserAction(BlockPointer pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> callback) {
		// Make sure that the anonymous class numbers didn't move around by checking for a SheepEntity reference.
	}
}
