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

package net.patchworkmc.impl.levelgenerators;

import java.io.File;
import java.util.concurrent.Executor;

import com.mojang.datafixers.DataFixer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.structure.StructureManager;

public class ChunkManagerValues {
	public static File file;
	public static DataFixer dataFixer;
	public static StructureManager structureManager;
	public static Executor workerExecutor;
	public static int viewDistance;
	public static WorldGenerationProgressListener progressListener;
	public static MinecraftServer server;
}
