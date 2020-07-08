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

package net.patchworkmc.mixin.containers;

import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.network.IContainerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.PacketByteBuf;

@Mixin(ContainerType.class)
public abstract class MixinContainerType implements IForgeContainerType<Container> {
	@Shadow
	@Final
	private ContainerType.Factory<?> factory;

	@Shadow
	public abstract Container create(int syncId, PlayerInventory playerInventory);

	@Override
	public Container create(int windowId, PlayerInventory playerInv, PacketByteBuf extraData) {
		if (this.factory instanceof IContainerFactory) {
			return ((IContainerFactory<?>) this.factory).create(windowId, playerInv, extraData);
		}

		return create(windowId, playerInv);
	}
}
