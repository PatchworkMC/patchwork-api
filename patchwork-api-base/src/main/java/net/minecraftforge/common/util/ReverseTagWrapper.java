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

package net.minecraftforge.common.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;

public class ReverseTagWrapper<T> {
	private final T target;
	private final Supplier<TagGroup<T>> colSupplier;

	//This map is immutable we track its identity change.
	private Map<Identifier, Tag<T>> colCache;
	private Set<Identifier> cache = null;

	public ReverseTagWrapper(T target, Supplier<TagGroup<T>> colSupplier) {
		this.target = target;
		this.colSupplier = colSupplier;
	}

	public Set<Identifier> getTagNames() {
		TagGroup<T> collection = colSupplier.get();

		// Identify equals
		if (cache == null || colCache != collection.getTags()) {
			this.cache = Collections.unmodifiableSet(new HashSet<>(collection.getTagsFor(target)));
			this.colCache = collection.getTags();
		}

		return this.cache;
	}
}
