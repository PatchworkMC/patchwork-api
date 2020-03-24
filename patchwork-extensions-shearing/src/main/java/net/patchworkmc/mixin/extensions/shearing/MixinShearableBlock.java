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

import net.minecraftforge.common.IShearable;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.CobwebBlock;
import net.minecraft.block.DeadBushBlock;
import net.minecraft.block.FernBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.ReplaceableTallPlantBlock;
import net.minecraft.block.SeagrassBlock;
import net.minecraft.block.VineBlock;

/**
 * Patches blocks to implement {@link IShearable}.
 *
 * @author SuperCoder79
 */
@Mixin({SeagrassBlock.class, VineBlock.class, LeavesBlock.class, DeadBushBlock.class, FernBlock.class, ReplaceableTallPlantBlock.class, CobwebBlock.class})
public class MixinShearableBlock implements IShearable {
}
