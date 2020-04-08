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

package net.patchworkmc.mixin.worldtypes;

import net.minecraftforge.common.extensions.IForgeWorldType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;

import net.patchworkmc.impl.worldtypes.LevelGeneratorTypeFactory;

@Mixin(OverworldDimension.class)
public abstract class MixinOverworldDimension extends Dimension {
	public MixinOverworldDimension(World world, DimensionType type) {
		super(world, type);
	}

	@Inject(method = "createChunkGenerator", at = @At("RETURN"), cancellable = true)
	private void createChunkGenerator(CallbackInfoReturnable<ChunkGenerator<? extends ChunkGeneratorConfig>> info) {
		LevelGeneratorType type = this.world.getLevelProperties().getGeneratorType();
		IForgeWorldType forgeWorldType = LevelGeneratorTypeFactory.getForgeWorldType(type);

		if (forgeWorldType != null) {
			info.setReturnValue(forgeWorldType.createChunkGenerator(this.world));
		}
	}
}
