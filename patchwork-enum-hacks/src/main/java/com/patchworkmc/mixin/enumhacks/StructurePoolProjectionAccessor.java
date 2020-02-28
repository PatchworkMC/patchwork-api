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

package com.patchworkmc.mixin.enumhacks;

import java.util.Map;

import com.google.common.collect.ImmutableList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.StructureProcessor;

@Mixin(StructurePool.Projection.class)
public interface StructurePoolProjectionAccessor {
	@Invoker("<init>")
	static StructurePool.Projection invokeConstructor(String name, int ordinal, String id, ImmutableList<StructureProcessor> processors) {
		throw new IllegalStateException("Mixin did not transform accessor! Something is very wrong!");
	}

	@Accessor("PROJECTIONS_BY_ID")
	static Map<String, StructurePool.Projection> getIdProjectionMap() {
		throw new IllegalStateException("Mixin did not transform accessor! Something is very wrong!");
	}
}
