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

package net.patchworkmc.mixin.levelgenerators;

import net.minecraftforge.common.extensions.IForgeWorldType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.level.LevelGeneratorType;

import net.patchworkmc.api.levelgenerators.PatchworkLevelGeneratorType;

@Mixin(Dimension.class)
public class MixinDimension {
	@Shadow
	@Final
	private World world;

	@Inject(at = @At("HEAD"), method = "getCloudHeight", cancellable = true)
	private void getCloudHeight(CallbackInfoReturnable<Float> info) {
		LevelGeneratorType generatorType = this.world.getLevelProperties().getGeneratorType();

		if (generatorType instanceof PatchworkLevelGeneratorType) {
			info.setReturnValue(((IForgeWorldType) generatorType).getCloudHeight());
		}
	}

	@Inject(at = @At("HEAD"), method = "getHorizonShadingRatio", cancellable = true)
	private void getHorizonShadingRatio(CallbackInfoReturnable<Double> info) {
		LevelGeneratorType generatorType = this.world.getLevelProperties().getGeneratorType();

		if (generatorType instanceof PatchworkLevelGeneratorType) {
			info.setReturnValue(((IForgeWorldType) generatorType).voidFadeMagnitude());
		}
	}
}
