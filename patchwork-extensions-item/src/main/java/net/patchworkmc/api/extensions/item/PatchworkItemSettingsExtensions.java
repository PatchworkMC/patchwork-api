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

package net.patchworkmc.api.extensions.item;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import net.minecraftforge.common.ToolType;

import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.item.Item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.patchworkmc.annotations.Stubbed;

/**
 * Forge does this through patching the constructor, we just add methods with
 * mixins instead.
 */
public interface PatchworkItemSettingsExtensions {
	@Stubbed
	Item.Settings setNoRepair();
	@Stubbed
	Item.Settings addToolType(ToolType type, int level);
	Item.Settings setISTER(Supplier<Callable<BuiltinModelItemRenderer>> ister);

	boolean patchwork$canRepair();
	Map<ToolType, Integer> patchwork$toolTypes();
	@Environment(EnvType.CLIENT)
	// Patchwork avoids the misdirection of Suppliers by just being smart about client only annotations
	BuiltinModelItemRenderer patchwork$ISTER();
}
