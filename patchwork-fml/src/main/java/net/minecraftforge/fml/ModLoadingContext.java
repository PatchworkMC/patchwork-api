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

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class ModLoadingContext {
	private static ThreadLocal<ModLoadingContext> context = ThreadLocal.withInitial(ModLoadingContext::new);
	private Object languageExtension;
	private ModContainer activeContainer;

	public static ModLoadingContext get() {
		return context.get();
	}

	public void setActiveContainer(final ModContainer container, final Object languageExtension) {
		this.activeContainer = container;
		this.languageExtension = languageExtension;
	}

	public ModContainer getActiveContainer() {
		if (activeContainer == null) {
			throw new UnsupportedOperationException("The default Minecraft ModContainer is unimplemented, no mod container is currently active");
		}

		return activeContainer;
	}

	public String getActiveNamespace() {
		if (activeContainer == null) {
			return "minecraft";
		} else {
			return activeContainer.getNamespace();
		}
	}

	public void registerConfig(ModConfig.Type type, ForgeConfigSpec spec) {
		getActiveContainer().addConfig(new ModConfig(type, spec, getActiveContainer()));
	}

	public void registerConfig(ModConfig.Type type, ForgeConfigSpec spec, String fileName) {
		getActiveContainer().addConfig(new ModConfig(type, spec, getActiveContainer(), fileName));
	}

	@SuppressWarnings("unchecked")
	public <T> T extension() {
		return (T) languageExtension;
	}
}
