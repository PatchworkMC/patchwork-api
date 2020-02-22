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

import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.fml.DistExecutor;

import net.minecraft.client.render.item.ItemDynamicRenderer;
import net.minecraft.item.Item;

import com.patchworkmc.impl.extension.PatchworkItemSettingsExtensions;

@Mixin(Item.class)
public abstract class MixinItem implements IForgeItem {
	@Unique
	@Nullable
	private Supplier<ItemDynamicRenderer> teisr;

	@Inject(at = @At("RETURN"), method = "<init>")
	private void onConstruct(Item.Settings settings, CallbackInfo info) {
		final PatchworkItemSettingsExtensions extension = (PatchworkItemSettingsExtensions) settings;

		final Object tmp = extension.getTeisr() == null ? null : DistExecutor.callWhenOn(Dist.CLIENT, extension.getTeisr());
		this.teisr = tmp == null ? null : () -> (ItemDynamicRenderer) tmp;
	}

	@Override
	public final ItemDynamicRenderer getTileEntityItemStackRenderer() {
		ItemDynamicRenderer renderer = teisr != null ? teisr.get() : null;
		return renderer != null ? renderer : ItemDynamicRenderer.INSTANCE;
	}
}
