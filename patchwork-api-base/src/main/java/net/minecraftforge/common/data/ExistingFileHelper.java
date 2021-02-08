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

package net.minecraftforge.common.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.patchworkmc.annotations.Stubbed;

/**
 * Enables data providers to check if other data files currently exist. The
 * instance provided in the {@link GatherDataEvent} utilizes the standard
 * resources (via {@link VanillaPack}), forge's resources, as well as any
 * extra resource packs passed in via the {@code --existing} argument,
 * or mod resources via the {@code --existing-mod} argument.
 */
@Stubbed
public class ExistingFileHelper {
	public interface IResourceType {
		net.minecraft.resource.ResourceType getPackType();

		String getSuffix();

		String getPrefix();
	}

	public static class ResourceType implements IResourceType {
		final net.minecraft.resource.ResourceType packType;
		final String suffix, prefix;
		public ResourceType(net.minecraft.resource.ResourceType type, String suffix, String prefix) {
			this.packType = type;
			this.suffix = suffix;
			this.prefix = prefix;
		}

		@Override
		public net.minecraft.resource.ResourceType getPackType() {
			return packType;
		}

		@Override
		public String getSuffix() {
			return suffix;
		}

		@Override
		public String getPrefix() {
			return prefix;
		}
	}

	private final ReloadableResourceManagerImpl clientResources, serverData;
	private final boolean enable;
	private final Multimap<net.minecraft.resource.ResourceType, Identifier> generated = HashMultimap.create();

	@Deprecated//TODO: Remove in 1.17
	public ExistingFileHelper(Collection<Path> existingPacks, boolean enable) {
		this(existingPacks, Collections.emptySet(), enable);
	}

	/**
	 * Create a new helper. This should probably <em>NOT</em> be used by mods, as
	 * the instance provided by forge is designed to be a central instance that
	 * tracks existence of generated data.
	 * <p>
	 * Only create a new helper if you intentionally want to ignore the existence of
	 * other generated files.
	 *
	 * @param existingPacks
	 * @param existingMods
	 * @param enable
	 */
	public ExistingFileHelper(Collection<Path> existingPacks, Set<String> existingMods, boolean enable) {
		throw new UnsupportedOperationException("This only exists for classloading purposes");
	}

	private ResourceManager getManager(net.minecraft.resource.ResourceType packType) {
		return packType == net.minecraft.resource.ResourceType.CLIENT_RESOURCES ? clientResources : serverData;
	}

	private Identifier getLocation(Identifier base, String suffix, String prefix) {
		return new Identifier(base.getNamespace(), prefix + "/" + base.getPath() + suffix);
	}

	/**
	 * Check if a given resource exists in the known resource packs.
	 *
	 * @param loc      the complete location of the resource, e.g.
	 *                 {@code "minecraft:textures/block/stone.png"}
	 * @param packType the type of resources to check
	 * @return {@code true} if the resource exists in any pack, {@code false}
	 *         otherwise
	 */
	public boolean exists(Identifier loc, net.minecraft.resource.ResourceType packType) {
		if (!enable) {
			return true;
		}

		return generated.get(packType).contains(loc) || getManager(packType).containsResource(loc);
	}

	/**
	 * Check if a given resource exists in the known resource packs. This is a
	 * convenience method to avoid repeating type/prefix/suffix and instead use the
	 * common definitions in {@link ResourceType}, or a custom {@link IResourceType}
	 * definition.
	 *
	 * @param loc  the base location of the resource, e.g.
	 *             {@code "minecraft:block/stone"}
	 * @param type a {@link IResourceType} describing how to form the path to the
	 *             resource
	 * @return {@code true} if the resource exists in any pack, {@code false}
	 *         otherwise
	 */
	public boolean exists(Identifier loc, IResourceType type) {
		return exists(getLocation(loc, type.getSuffix(), type.getPrefix()), type.getPackType());
	}

	/**
	 * Check if a given resource exists in the known resource packs.
	 *
	 * @param loc        the base location of the resource, e.g.
	 *                   {@code "minecraft:block/stone"}
	 * @param packType   the type of resources to check
	 * @param pathSuffix a string to append after the path, e.g. {@code ".json"}
	 * @param pathPrefix a string to append before the path, before a slash, e.g.
	 *                   {@code "models"}
	 * @return {@code true} if the resource exists in any pack, {@code false}
	 *         otherwise
	 */
	public boolean exists(Identifier loc, net.minecraft.resource.ResourceType packType, String pathSuffix, String pathPrefix) {
		return exists(getLocation(loc, pathSuffix, pathPrefix), packType);
	}

	/**
	 * Track the existence of a generated file. This is a convenience method to
	 * avoid repeating type/prefix/suffix and instead use the common definitions in
	 * {@link ResourceType}, or a custom {@link IResourceType} definition.
	 * <p>
	 * This should be called by data providers immediately when a new data object is
	 * created, i.e. not during
	 * {@link IDataProvider#act(net.minecraft.data.DirectoryCache) act} but instead
	 * when the "builder" (or whatever intermediate object) is created, such as a
	 * {@link ModelBuilder}.
	 * <p>
	 * This represents a <em>promise</em> to generate the file later, since other
	 * datagen may rely on this file existing.
	 *
	 * @param loc  the base location of the resource, e.g.
	 *             {@code "minecraft:block/stone"}
	 * @param type a {@link IResourceType} describing how to form the path to the
	 *             resource
	 */
	public void trackGenerated(Identifier loc, IResourceType type) {
		this.generated.put(type.getPackType(), getLocation(loc, type.getSuffix(), type.getPrefix()));
	}

	/**
	 * Track the existence of a generated file.
	 * <p>
	 * This should be called by data providers immediately when a new data object is
	 * created, i.e. not during
	 * {@link IDataProvider#act(net.minecraft.data.DirectoryCache) act} but instead
	 * when the "builder" (or whatever intermediate object) is created, such as a
	 * {@link ModelBuilder}.
	 * <p>
	 * This represents a <em>promise</em> to generate the file later, since other
	 * datagen may rely on this file existing.
	 *
	 * @param loc        the base location of the resource, e.g.
	 *                   {@code "minecraft:block/stone"}
	 * @param packType   the type of resources to check
	 * @param pathSuffix a string to append after the path, e.g. {@code ".json"}
	 * @param pathPrefix a string to append before the path, before a slash, e.g.
	 *                   {@code "models"}
	 */
	public void trackGenerated(Identifier loc, net.minecraft.resource.ResourceType packType, String pathSuffix, String pathPrefix) {
		this.generated.put(packType, getLocation(loc, pathSuffix, pathPrefix));
	}

	@VisibleForTesting
	public Resource getResource(Identifier loc, net.minecraft.resource.ResourceType packType, String pathSuffix, String pathPrefix) throws
			IOException {
		return getResource(getLocation(loc, pathSuffix, pathPrefix), packType);
	}

	@VisibleForTesting
	public Resource getResource(Identifier loc, net.minecraft.resource.ResourceType packType) throws IOException {
		return getManager(packType).getResource(loc);
	}

	/**
	 * @return {@code true} if validation is enabled, {@code false} otherwise
	 */
	public boolean isEnabled() {
		return enable;
	}
}
