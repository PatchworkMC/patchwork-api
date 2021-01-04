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

package net.patchworkmc.mixin.capability;

import java.util.List;

import net.minecraftforge.common.capabilities.CapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.World;

import net.patchworkmc.impl.capability.BaseCapabilityProvider;
import net.patchworkmc.impl.capability.CapabilityProviderHolder;
import net.patchworkmc.impl.capability.IForgeTileEntityDuck;

@Mixin(World.class)
public class MixinWorld implements CapabilityProviderHolder {
	@Shadow
	@Final
	protected List<BlockEntity> unloadedBlockEntities;
	@Unique
	private final CapabilityProvider<?> internalProvider = new BaseCapabilityProvider<>(BlockEntity.class, this);

	@NotNull
	@Override
	public CapabilityProvider<?> patchwork$getCapabilityProvider() {
		return internalProvider;
	}

	@Inject(method = "tickBlockEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;tickingBlockEntities:Ljava/util/List;",
			ordinal = 0, opcode = Opcodes.GETFIELD))
	private void patchwork$callOnChunkUnloaded(CallbackInfo ci) {
		for (BlockEntity unloadedBlockEntity : this.unloadedBlockEntities) {
			((IForgeTileEntityDuck) unloadedBlockEntity).onChunkUnloaded();
		}
	}
}
