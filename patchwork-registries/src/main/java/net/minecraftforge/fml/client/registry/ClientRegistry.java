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

package net.minecraftforge.fml.client.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.client.render.BlockEntityRendererRegistry;

import com.patchworkmc.impl.registries.AddedKeybinds;

public class ClientRegistry {
	private static Map<Class<? extends Entity>, Identifier> entityShaderMap = new ConcurrentHashMap<>();

	/**
	 * Registers a {@link BlockEntityRenderer}
	 * Call this during {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}.
	 * This method is safe to call during parallel mod loading.
	 */
	public static synchronized <T extends BlockEntity> void bindTileEntitySpecialRenderer(Class<T> tileEntityClass, BlockEntityRenderer<? super T> specialRenderer) {
		BlockEntityRendererRegistry.INSTANCE.register(tileEntityClass, specialRenderer);
		specialRenderer.setRenderManager(BlockEntityRenderDispatcher.INSTANCE);
	}

	/**
	 * Registers a {@link KeyBinding}.
	 * Call this during {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}.
	 * This method is safe to call during parallel mod loading.
	 */
	public static synchronized void registerKeyBinding(KeyBinding key) {
		KeyBindingRegistry.INSTANCE.addCategory(key.getCategory());
		AddedKeybinds.registerKeyBinding(key);
	}

	/**
	 * Register a shader for an entity. This shader gets activated when a spectator begins spectating an entity.
	 * Vanilla examples of this are the green effect for creepers and the invert effect for endermen.
	 * Call this during {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}.
	 * This method is safe to call during parallel mod loading.
	 */
	public static void registerEntityShader(Class<? extends Entity> entityClass, Identifier shader) {
		entityShaderMap.put(entityClass, shader);
	}

	public static Identifier getEntityShader(Class<? extends Entity> entityClass) {
		return entityShaderMap.get(entityClass);
	}
}
