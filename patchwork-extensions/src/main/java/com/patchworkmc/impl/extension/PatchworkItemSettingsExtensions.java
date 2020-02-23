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

package com.patchworkmc.impl.extension;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import net.minecraft.client.render.item.ItemDynamicRenderer;
import net.minecraft.item.Item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Forge does this through patching the constructor, we just add methods with
 * mixins instead.
 */
public interface PatchworkItemSettingsExtensions {
	Item.Settings setNoRepair();

	Item.Settings addToolType(Object /* TODO: ToolType */ type, int level);

	Item.Settings setTEISR(Supplier<Callable<ItemDynamicRenderer>> teisr);

	boolean canRepair();

	Map<Object /* TODO: ToolType */, Integer> getToolClasses();

	@Environment(EnvType.CLIENT)
	ItemDynamicRenderer getTeisr();
}
