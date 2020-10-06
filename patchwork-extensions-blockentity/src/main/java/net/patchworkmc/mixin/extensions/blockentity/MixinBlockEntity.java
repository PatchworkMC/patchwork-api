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

package net.patchworkmc.mixin.extensions.blockentity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraftforge.common.extensions.IForgeTileEntity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;

@Mixin(BlockEntity.class)
public abstract class MixinBlockEntity implements IForgeTileEntity {
	@Unique
	private CompoundTag customTileData;

	@Inject(method = "fromTag", at = @At("RETURN"))
	private void readForgeData(CompoundTag tag, CallbackInfo ci) {
		if (tag.contains("ForgeData")) {
			customTileData = tag.getCompound("ForgeData");
		}
	}

	@Inject(method = "writeIdentifyingData", at = @At("RETURN"))
	private void saveForgeData(CompoundTag tag, CallbackInfoReturnable<CompoundTag> ci) {
		if (customTileData != null) {
			tag.put("ForgeData", customTileData);
		}
	}

	@Inject(method = "markRemoved", at = @At("RETURN"))
	private void markRemoved(CallbackInfo ci) {
		requestModelDataUpdate();
	}

	@Override
	public CompoundTag getTileData() {
		if (customTileData == null) {
			customTileData = new CompoundTag();
		}

		return customTileData;
	}
}
