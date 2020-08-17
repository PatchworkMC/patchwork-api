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

package net.patchworkmc.mixin.registries;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.Int2ObjectBiMap;

import net.patchworkmc.impl.registries.RemovableInt2ObjectBiMap;

@Mixin(Int2ObjectBiMap.class)
public abstract class MixinInt2ObjectBiMap<K> implements RemovableInt2ObjectBiMap<K> {
	@Shadow
	private K[] values;
	@Shadow
	private int[] ids;
	@Shadow
	private K[] idToValues;
	@Shadow
	private int nextId;

	@Shadow
	private int findIndex(@Nullable K object, int i) {
		return -1;
	}

	@Shadow
	private int getIdealIndex(@Nullable K object) {
		return -1;
	}

	@Override
	public int patchwork$remove(K object) {
		Int2ObjectBiMap<K> me = (Int2ObjectBiMap<K>) (Object) this;
		int index = getIdealIndex(object);
		index = findIndex(object, index);

		if (index == -1) {
			return -1;
		}

		int rawId = this.ids[index];

		this.idToValues[rawId] = null;
		this.ids[index] = -1;
		this.values[index] = null;
		this.nextId = 0;

		return rawId;
	}
}
