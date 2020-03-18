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

package net.patchworkmc.mixin.enumhacks;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.world.gen.feature.OreFeatureConfig;

import net.patchworkmc.impl.enumhacks.HackableEnum;

@Mixin(OreFeatureConfig.Target.class)
public class MixinOreFeatureConfigTarget implements HackableEnum<OreFeatureConfig.Target> {
	@Shadow
	@Final
	@Mutable
	private static OreFeatureConfig.Target[] field_13729;

	@Override
	public void patchwork_setValues(OreFeatureConfig.Target[] values) {
		field_13729 = values;
	}
}
