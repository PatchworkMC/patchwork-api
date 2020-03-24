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

package net.patchworkmc.mixin.enumhacks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CollisionView;
import net.minecraft.world.SpawnHelper;

import net.patchworkmc.impl.enumhacks.PatchworkSpawnRestrictionLocation;

@Mixin(SpawnHelper.class)
public class MixinSpawnHelper {
	@Inject(method = "canSpawn(Lnet/minecraft/entity/SpawnRestriction$Location;Lnet/minecraft/world/CollisionView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/EntityType;)Z",
			at = @At(value = "INVOKE", target = "net/minecraft/world/CollisionView.getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"),
			cancellable = true)
	private static void handleCustomSpawnRestrictionLocation(SpawnRestriction.Location location, CollisionView world, BlockPos pos, EntityType<?> type, CallbackInfoReturnable<Boolean> callback) {
		PatchworkSpawnRestrictionLocation patchworkLocation = (PatchworkSpawnRestrictionLocation) (Object) location;

		if (patchworkLocation.patchwork_useVanillaBehavior()) {
			return;
		}

		callback.setReturnValue(patchworkLocation.canSpawnAt(world, pos, type));
	}
}
