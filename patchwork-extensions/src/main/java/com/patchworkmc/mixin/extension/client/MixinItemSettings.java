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

package com.patchworkmc.mixin.extension.client;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.render.item.ItemDynamicRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;

import com.patchworkmc.impl.extension.PatchworkItemSettingsExtensions;

@Mixin(Item.Settings.class)
public abstract class MixinItemSettings implements PatchworkItemSettingsExtensions {
	@Unique private Supplier<Callable<ItemDynamicRenderer>> teisr;

	@Override
	public Settings setTEISR(Supplier<Callable<ItemDynamicRenderer>> teisr) {
		this.teisr = teisr;
		return (Settings) (Object) this;
	}

	@Override
	public Supplier<Callable<ItemDynamicRenderer>> getTeisr() {
		return teisr;
	}
}
