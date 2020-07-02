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

package net.patchworkmc.mixin.extensions.block.blockentity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.entity.FallingBlockEntity;

import net.patchworkmc.impl.extensions.block.BlockContext;

@Mixin(FallingBlockEntity.class)
public abstract class MixinFallingBlockEntity {
	////////////////////////
	/// tick()
	////////////////////////
	// } else if (block2 != block && block2 instanceof BlockEntityProvider) {
	@ModifyConstant(method = "tick", constant = @Constant(classValue = BlockEntityProvider.class, ordinal = 0))
	private boolean patchwork_tick_instanceof_BlockEntityProvider(Object object, Class<?> clazz) {
		FallingBlockEntity me = (FallingBlockEntity) (Object) this;
		// Forge: return ((Block) object).hasBlockEntity();
		return BlockContext.hasBlockEntity(me.getBlockState());
	}
}
