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

import net.minecraftforge.common.extensions.IForgeItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.item.Item;

import net.patchworkmc.api.extensions.item.PatchworkItemSettingsExtensions;

@Mixin(Item.class)
public abstract class MixinItem implements IForgeItem {
	@Unique
	@Nullable
	private BuiltinModelItemRenderer ister;

	@Inject(at = @At("RETURN"), method = "<init>")
	private void onConstruct(Item.Settings settings, CallbackInfo info) {
		final PatchworkItemSettingsExtensions extension = (PatchworkItemSettingsExtensions) settings;
		this.ister = extension.patchwork$ISTER();
	}

	@Override
	public BuiltinModelItemRenderer getItemStackTileEntityRenderer() {
		return ister == null ? BuiltinModelItemRenderer.INSTANCE : ister;
	}
}
