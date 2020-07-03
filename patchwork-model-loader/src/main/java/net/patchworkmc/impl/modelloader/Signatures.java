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

package net.patchworkmc.impl.modelloader;

public class Signatures {
	public static final String Profiler_swap = "net/minecraft/util/profiler/Profiler.swap(Ljava/lang/String;)V";

	public static final String ModelLoader_new = "("
			+ "Lnet/minecraft/resource/ResourceManager;"
			+ "Lnet/minecraft/client/texture/SpriteAtlasTexture;"
			+ "Lnet/minecraft/client/color/block/BlockColors;"
			+ "Lnet/minecraft/util/profiler/Profiler;"
			+ ")"
			+ "Lnet/minecraft/client/render/model/ModelLoader;";

	public static final String ModelLoader_addModel = "net/minecraft/client/render/model/ModelLoader.addModel(Lnet/minecraft/client/util/ModelIdentifier;)V";
}
