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

package net.patchworkmc.mixin.event.world.blocksnapshot;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.patchworkmc.impl.event.world.BlockSnapshotStateAccess;

@Mixin(Block.class)
public class MixinBlock {
	@Inject(method = "dropStack", at = @At(value = "FIELD", args = "floatValue=0.5"), cancellable = true)
	private static void shortCircuitDropStack(World world, BlockPos pos, ItemStack stack, CallbackInfo ci) {
		if (((BlockSnapshotStateAccess) world).patchwork$restoringBlockSnapshots()) {
			ci.cancel();
		}
	}

	@Inject(method = "dropExperience", at = @At(value = "JUMP", opcode = Opcodes.IFLE), cancellable = true)
	private void shortCircuitDropExperience(World world, BlockPos pos, int size, CallbackInfo ci) {
		if (((BlockSnapshotStateAccess) world).patchwork$restoringBlockSnapshots()) {
			ci.cancel();
		}
	}
}
