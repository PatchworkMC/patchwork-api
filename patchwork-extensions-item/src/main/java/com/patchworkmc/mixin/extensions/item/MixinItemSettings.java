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

package com.patchworkmc.mixin.extensions.item;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;

import com.patchworkmc.impl.extensions.item.PatchworkItemSettingsExtensions;

@Mixin(Item.Settings.class)
public abstract class MixinItemSettings implements PatchworkItemSettingsExtensions {
	@Unique private boolean canRepair = true;
	@Unique private final Map<Object /* TODO: ToolType */, Integer> toolClasses = new HashMap<>();

	@Override
	public Settings setNoRepair() {
		canRepair = false;
		return (Settings) (Object) this;
	}

	@Override
	public Settings addToolType(Object /* TODO: ToolType */ type, int level) {
		toolClasses.put(type, level);
		return (Settings) (Object) this;
	}

	@Override
	public boolean canRepair() {
		return canRepair;
	}

	@Override
	public Map<Object /* TODO: ToolType */, Integer> getToolClasses() {
		return toolClasses;
	}
}
