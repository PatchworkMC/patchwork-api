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

package net.patchworkmc.impl.extensions.entity;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockApplyCallback;

import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import net.minecraftforge.common.extensions.IForgeEntity;

public class EntityExtensionsInitializer implements ClientModInitializer {
	/**
	 * Allow the result of {@link IForgeEntity#getPickedResult(HitResult)} to replace vanilla behavior on entity
	 * picking. This implementation allows fabric mods to control the picked item through
	 * {@link net.fabricmc.fabric.api.event.client.player.ClientPickBlockGatherCallback}.
	 */
	@Override
	public void onInitializeClient() {
		ClientPickBlockApplyCallback.EVENT.register((playerEntity, hitResult, itemStack) -> {
			if (hitResult instanceof EntityHitResult) {
				return ((IForgeEntity) ((EntityHitResult) hitResult).getEntity()).getPickedResult(hitResult);
			} else {
				return itemStack;
			}
		});
	}
}
