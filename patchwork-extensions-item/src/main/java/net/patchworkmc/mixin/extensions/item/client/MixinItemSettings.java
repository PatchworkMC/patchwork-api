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

package net.patchworkmc.mixin.extensions.item.client;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.item.Item;

import net.patchworkmc.api.extensions.item.PatchworkItemSettingsExtensions;

@Mixin(Item.Settings.class)
public abstract class MixinItemSettings implements PatchworkItemSettingsExtensions {
	@Unique
	private BuiltinModelItemRenderer ister;

	// NOTE: technically this is available on the server for forge
	// you can tell cpw wrote this code because it abuses classloading and lambdas over @OnlyIn(Dist.CLIENT)
	@Override
	public Item.Settings setISTER(Supplier<Callable<BuiltinModelItemRenderer>> ister) {
		try {
			if (ister != null) {
				this.ister = ister.get().call();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return (Item.Settings) (Object) this;
	}

	@Override
	public BuiltinModelItemRenderer patchwork$ISTER() {
		return ister;
	}
}
