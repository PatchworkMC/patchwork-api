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

import java.util.Map;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.OreFeatureConfig;

@Mixin(OreFeatureConfig.Target.class)
public interface OreFeatureConfigTargetAccessor {
	@Invoker("<init>")
	static OreFeatureConfig.Target invokeConstructor(String constantName, int ordinal, String name, Predicate<BlockState> predicate) {
		throw new IllegalStateException("Mixin did not transform accessor! Something is very wrong!");
	}

	@Accessor("nameMap")
	static Map<String, OreFeatureConfig.Target> getNameMap() {
		throw new IllegalStateException("Mixin did not transform accessor! Something is very wrong!");
	}
}
