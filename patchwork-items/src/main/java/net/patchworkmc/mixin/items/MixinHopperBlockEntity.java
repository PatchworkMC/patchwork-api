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

package net.patchworkmc.mixin.items;

import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.patchworkmc.impl.items.PatchworkItems;
import net.patchworkmc.impl.items.ItemHandlerInventoryWrapper;

@Mixin(HopperBlockEntity.class)
public class MixinHopperBlockEntity {
	@Inject(method = "getInputInventory", at = @At("HEAD"))
	private static void onGetInputInventory(Hopper hopper, CallbackInfoReturnable<Inventory> cir) {
		PatchworkItems.currentSide.set(Direction.UP);
	}

	@Inject(method = "getOutputInventory", at = @At("HEAD"))
	private void onGetOutputInventory(CallbackInfoReturnable<Inventory> cir) {
		PatchworkItems.currentSide.set(((BlockEntity) (Object) this).getCachedState().get(HopperBlock.FACING));
	}

	@Inject(method = "getInventoryAt(Lnet/minecraft/world/World;DDD)Lnet/minecraft/inventory/Inventory;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;)Ljava/util/List;"), cancellable = true)
	private static void onGetInventoryAt(World world, double x, double y, double z, CallbackInfoReturnable<Inventory> cir) {
		BlockPos blockPos = new BlockPos(x, y, z);
		BlockState blockState = world.getBlockState(blockPos);
		Block block = blockState.getBlock();

		if (block.hasBlockEntity()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);

			if (blockEntity != null) {
				Direction direction = PatchworkItems.currentSide.get();

				if (direction != null) {
					LazyOptional<IItemHandler> capability = ((ICapabilityProvider) blockEntity).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite());
					PatchworkItems.currentSide.remove();
					capability.ifPresent(result -> {
						if (result instanceof IItemHandlerModifiable) {
							cir.setReturnValue(new ItemHandlerInventoryWrapper((IItemHandlerModifiable) result));
						}
					});
				}
			}
		}
	}

	@Inject(method = "transfer(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/item/ItemStack;ILnet/minecraft/util/math/Direction;)Lnet/minecraft/item/ItemStack;", at = @At("HEAD"), cancellable = true)
	private static void onTransfer(Inventory from, Inventory to, ItemStack stack, int slot, Direction direction, CallbackInfoReturnable<ItemStack> cir) {
		IItemHandlerModifiable itemHandler;

		if (to instanceof ItemHandlerInventoryWrapper) {
			itemHandler = ((ItemHandlerInventoryWrapper) to).getItemHandler();
			cir.setReturnValue(itemHandler.insertItem(slot, stack, false));
		}
	}
}
