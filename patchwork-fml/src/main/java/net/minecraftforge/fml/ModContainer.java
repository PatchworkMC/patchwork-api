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

package net.minecraftforge.fml;

import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO: Stub
public abstract class ModContainer {
	protected static final Logger LOGGER = LogManager.getLogger(ModContainer.class);
	protected final String modId;
	protected final String namespace;
	protected final Map<ExtensionPoint, Supplier<?>> extensionPoints = new IdentityHashMap<>();
	protected final EnumMap<ModConfig.Type, ModConfig> configs = new EnumMap<>(ModConfig.Type.class);
	private net.fabricmc.loader.api.ModContainer fabricModContainer;
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	protected Optional<Consumer<ModConfig.ModConfigEvent>> configHandler = Optional.empty();

	public ModContainer(String modId) {
		this.modId = modId;
		// TODO: Currently not reading namespace from configuration..
		this.namespace = modId;
		//this.configs = new EnumMap<>(ModConfig.Type.class);
	}

	public final String getModId() {
		return modId;
	}

	public final String getNamespace() {
		return namespace;
	}

	@SuppressWarnings("unchecked")
	public <T> Optional<T> getCustomExtension(ExtensionPoint<T> point) {
		return Optional.ofNullable((T) extensionPoints.getOrDefault(point, () -> null).get());
	}

	public <T> void registerExtensionPoint(ExtensionPoint<T> point, Supplier<T> extension) {
		extensionPoints.put(point, extension);

		if (point == ExtensionPoint.DISPLAYTEST) {
			LOGGER.warn(
					"ExtensionPoint.DISPLAYTEST is not handled by Patchwork due to the limitation of Fabric, "
					+ "we cannot prevent loading mods at wrong side or incompatible versions!");
		}
	}

	public final void patchwork$setParent(net.fabricmc.loader.api.ModContainer fabricModContainer) {
		this.fabricModContainer = fabricModContainer;
	}

	public final net.fabricmc.loader.api.ModContainer patchwork$getParent() {
		return this.fabricModContainer;
	}

	public void addConfig(final ModConfig modConfig) {
		configs.put(modConfig.getType(), modConfig);
	}

	public void dispatchConfigEvent(ModConfig.ModConfigEvent event) {
		configHandler.ifPresent(configHandler -> configHandler.accept(event));
	}

	public abstract Object getMod();

	protected void acceptEvent(Event e) {
	}

	public final void patchwork$acceptEvent(Event e) {
		this.acceptEvent(e);
	}
}
