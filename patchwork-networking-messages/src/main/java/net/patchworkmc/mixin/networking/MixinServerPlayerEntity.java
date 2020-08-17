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

package net.patchworkmc.mixin.networking;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.network.ServerPlayerEntity;

import net.patchworkmc.impl.networking.ContainerSyncAccess;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity implements ContainerSyncAccess {
	@Shadow
	private int containerSyncId;
	@Shadow
	protected abstract void incrementContainerSyncId();

	@Override
	public int patchwork$getNewContainerSyncId() {
		incrementContainerSyncId();

		return containerSyncId;
	}
}
