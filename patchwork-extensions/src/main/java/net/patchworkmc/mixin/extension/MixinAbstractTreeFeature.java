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

package net.patchworkmc.mixin.extension;

import net.minecraftforge.common.extensions.IForgeBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.util.math.BlockPos;

@Mixin(TreeFeature.class)
public abstract class MixinAbstractTreeFeature {
	@Shadow
	protected abstract void setToDirt(ModifiableTestableWorld world, BlockPos pos);

	// TODO: How can we make this accessible in fabric mods?
	protected void setDirtAt(ModifiableTestableWorld reader, BlockPos pos, BlockPos origin) {
		if (!(reader instanceof WorldAccess)) {
			setToDirt(reader, pos);
			return;
		}

		((IForgeBlockState) ((WorldAccess) reader).getBlockState(pos)).onPlantGrow((WorldAccess) reader, pos, origin);
	}
}
