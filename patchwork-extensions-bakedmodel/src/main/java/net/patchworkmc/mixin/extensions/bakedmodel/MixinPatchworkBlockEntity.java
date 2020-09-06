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

package net.patchworkmc.mixin.extensions.bakedmodel;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraftforge.client.model.ModelDataManager;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.World;

import net.patchworkmc.impl.extensions.bakedmodel.ForgeModelDataProvider;
import net.patchworkmc.impl.extensions.blockentity.PatchworkBlockEntity;

/**
 * Need this mixin to maintain modularity and resolve circular dependency.
 */
@Mixin(PatchworkBlockEntity.class)
public interface MixinPatchworkBlockEntity extends PatchworkBlockEntity, ForgeModelDataProvider {
	/**
	 * Requests a refresh for the model data of your TE
	 * Call this every time your {@link #getModelData()} changes.
	 */
	@Override
	default void requestModelDataUpdate() {
		BlockEntity te = (BlockEntity) (Object) this;
		World world = te.getWorld();

		if (world != null && world.isClient) {
			ModelDataManager.requestModelDataRefresh(te);
		}
	}
}
