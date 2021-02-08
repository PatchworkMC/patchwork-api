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

package net.patchworkmc.impl.extensions.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Transforms BlockEntityProvider calls to a Block call. Technically unsafe, but the crash log will be polluted by Patchwork.
 */
class TileEntityHacks {
	private static final String BLOCK_ENTITY_PROVIDER = Transformer.mapSlashedClass("net/minecraft/block/BlockEntityProvider");
	// it's easier to just hardcode this method name that will never change since we don't need the desc
	private static final String CREATE_BE =
			FabricLoader.getInstance().isDevelopmentEnvironment() ? "createBlockEntity" : "method_10123";
	private static final String BLOCK = Transformer.mapSlashedClass("net/minecraft/class_2248");

	public static void classLoadMePlease() {
		//
	}

	public static boolean transform(MethodNode node) {
		boolean didSomething = false;

		for (AbstractInsnNode instruction : node.instructions) {
			if (instruction.getOpcode() == Opcodes.CHECKCAST) {
				TypeInsnNode tin = (TypeInsnNode) instruction;

				if (tin.desc.equals(BLOCK_ENTITY_PROVIDER)) {
					// this could be problematic for weird use cases. too bad!
					node.instructions.remove(tin);
				}
			} else if (instruction.getOpcode() == Opcodes.INVOKEINTERFACE) {
				MethodInsnNode min = (MethodInsnNode) instruction;

				if (min.owner.equals(BLOCK_ENTITY_PROVIDER) && min.name.equals(CREATE_BE)) {
					min.owner = BLOCK;
					min.name = "patchwork$createTileEntityIntermediate";
					min.itf = false;
					min.setOpcode(Opcodes.INVOKEVIRTUAL);
					didSomething = true;
				}
			}
		}

		return didSomething;
	}
}
