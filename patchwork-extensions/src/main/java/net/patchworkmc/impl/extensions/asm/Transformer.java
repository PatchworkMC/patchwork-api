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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Handles the case of ()Lsomething; -> (Lstate;)Lsomething;.
 * Able to detect local variables and direct chaining: stack.getItem().getMaxDamage().
 * Does not properly handle methods that have parameters.
 */
class Transformer {
	private static final FabricLoader LOADER = FabricLoader.getInstance();
	private static final boolean CHANGE_ME_TO_AUDIT = true;
	private static final boolean AUDIT = FabricLoader.getInstance().isDevelopmentEnvironment() && CHANGE_ME_TO_AUDIT;

	public static final Logger LOGGER = LogManager.getLogger("Patchwork Mass ASM");
	public static List<RedirectTarget> targets = new ArrayList<>();

	public static void init() {
		BaseToContextMapper.classLoadMePlease();
		InstanceFinder.classLoadMePlease();
		TileEntityHacks.classLoadMePlease();
		String ITEM_STACK_CLASS = "net/minecraft/class_1799";
		String ITEM_CLASS = "net/minecraft/class_1792";
		Method[] itemConversions = {
				new Method(ITEM_STACK_CLASS, "method_7909", "()Lnet/minecraft/class_1792;"),
		};
		targets.add(new RedirectTarget(ITEM_CLASS, ITEM_STACK_CLASS,
				new Method(ITEM_CLASS, "method_7841", "()I"),
				"getMaxDamage", itemConversions));
		targets.add(new RedirectTarget(ITEM_CLASS, ITEM_STACK_CLASS,
				new Method(ITEM_CLASS, "method_7882", "()I"),
				"patchwork$getItemStackLimit", itemConversions));
		// TODO: if someone uses AbstractBlockState this doesn't work
		String BLOCKSTATE_CLASS = "net/minecraft/class_2680";
		String BLOCK_CLASS = "net/minecraft/class_2248";
		Method[] blockConversions = {
				// since our mappings aren't propagated we have to hardcode this method name. too bad!
				new Method(BLOCKSTATE_CLASS,
						LOADER.isDevelopmentEnvironment() ? "getBlock" : "method_26204", "()Lnet/minecraft/class_2248;")
		};
		targets.add(new RedirectTarget(BLOCK_CLASS, BLOCKSTATE_CLASS,
				new Method(BLOCK_CLASS, LOADER.isDevelopmentEnvironment() ? "hasBlockEntity" : "method_26161", "()Z"),
				"hasTileEntity",
				blockConversions));
		// TileEntityHacks is what lets use do this
		targets.add(new RedirectTarget(BLOCK_CLASS, BLOCKSTATE_CLASS,
				new Method(BLOCK_CLASS, "patchwork$createTileEntityIntermediate", "(Lnet/minecraft/class_1922;)Lnet/minecraft/class_2586;"),
				"createTileEntity",
				blockConversions));
	}

	public static byte[] transform(String name, byte[] in) throws AnalyzerException {
		name = name.replace('.', '/');

		// Don't process our extension classes or we get nasty recursion errors
		if (name.startsWith("net/minecraftforge/common/extensions")) {
			return in;
		}

		boolean didSomething = false;
		ClassReader reader = new ClassReader(in);
		ClassNode node = new ClassNode();
		reader.accept(node, ClassReader.EXPAND_FRAMES);

		for (MethodNode method : node.methods) {
			didSomething = TileEntityHacks.transform(method) | /* one pipe to not short circuit*/ scan(method, name) || didSomething;
		}

		byte[] out = in;

		if (didSomething) {
			ClassWriter writer = new KnotClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			node.accept(writer);
			out = writer.toByteArray();
		}

		if (AUDIT && didSomething) {
			try {
				Path target = new File("./audit/").toPath().resolve(name + ".class");
				Files.createDirectories(target.getParent());
				Files.write(target, out);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return out;
	}

	private static boolean scan(MethodNode method, String className) throws AnalyzerException {
		boolean didSomething = false;

		for (RedirectTarget redirect : targets) {
			if (className.equals(redirect.baseClass) || className.equals(redirect.contextClass)) {
				continue;
			}

			List<MethodInsnNode> minTargets = findTargets(className, method, redirect);

			if (minTargets.isEmpty()) {
				continue;
			}

			Map<Integer, Integer> conversionMap = null;

			for (MethodInsnNode targetNode : minTargets) {
				try {
					new InstanceFinder(targetNode).analyze(className, method);
				} catch (AnalyzerException e) {
					if (!(e.getCause() instanceof InstanceFinder.FoundException)) {
						throw e;
					}

					for (AbstractInsnNode creator : ((InstanceFinder.FoundException) e.getCause()).insns) {
						if (creator instanceof VarInsnNode) {
							VarInsnNode vin = (VarInsnNode) creator;

							if (conversionMap == null) {
								conversionMap = createBaseToContextMapping(method, className, redirect);
							}

							Integer translation = conversionMap.get(vin.var);

							if (translation == null) {
								LOGGER.warn("Found a target in " + className + "::" + method.name + method.desc + ", but not a conversion method!");
								continue;
							}

							// load the context instead
							vin.var = translation;
							// call our new method
							targetNode.owner = redirect.contextClass;
							targetNode.name = redirect.newTargetName;
							didSomething = true;
						} else if (creator instanceof MethodInsnNode) {
							if (!redirect.isConversionMethod((MethodInsnNode) creator)) {
								LOGGER.warn("Found a target in " + className + "::" + method.name + method.desc + ", but not a conversion method!");
								continue;
							}

							// since this is context.convert().targetMethod()
							// we can just remove the convert() and then we have context.newMethod()
							method.instructions.remove(creator);
							targetNode.owner = redirect.contextClass;
							targetNode.name = redirect.newTargetName;
							didSomething = true;
						}
					}

					continue;
				}

				throw new IllegalStateException("Did not find instance, something is very wrong!");
			}
		}

		return didSomething;
	}

	private static Map<Integer, Integer> createBaseToContextMapping(MethodNode method, String className, RedirectTarget redirect)
			throws AnalyzerException {
		BaseToContextMapper finder = new BaseToContextMapper(redirect);
		finder.analyze(className, method);
		return finder.translations;
	}

	private static List<MethodInsnNode> findTargets(String className, MethodNode method, RedirectTarget target) {
		boolean conversion = false;
		ArrayList<MethodInsnNode> targets = new ArrayList<>();

		for (AbstractInsnNode instruction : method.instructions) {
			if (instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				MethodInsnNode insn = (MethodInsnNode) instruction;

				if (!conversion) {
					for (Method conversionMethod : target.conversionMethods) {
						conversion = conversionMethod.matches(insn);
					}
				}

				if (target.target.matches(insn)) {
					targets.add(insn);
				}
			}
		}

		if (!conversion && !targets.isEmpty()) {
			LOGGER.debug("Found a target in " + className + "::" + method.name + method.desc + ", but not a conversion method!");

			return Collections.emptyList();
		} else {
			return targets;
		}
	}

	public static String mapSlashedClass(String in) {
		return LOADER.getMappingResolver().mapClassName("intermediary", in.replace('/', '.')).replace('.', '/');
	}

	public static String mapDesc(String desc) {
		Type type = Type.getMethodType(desc);
		StringBuilder sb = new StringBuilder("(");

		for (Type argumentType : type.getArgumentTypes()) {
			sb.append(mapAsmType(argumentType.getClassName()));
		}

		sb.append(")").append(mapAsmType(type.getReturnType().getClassName()));

		return sb.toString();
	}

	private static String mapAsmType(String argumentType) {
		switch (argumentType) {
			case "void":
				return "V";
			case "boolean":
				return "Z";
			case "char":
				return "C";
			case "byte":
				return "B";
			case "int":
				return "I";
			case "float":
				return "F";
			case "long":
				return "J";
			case "double":
				return "L";
			default:
				return "L" + mapSlashedClass(argumentType) + ";";
		}
	}

	static class RedirectTarget {
		public final String baseClass;
		public final String contextClass;
		public final Method target;
		public final String newTargetName;
		public final Method[] conversionMethods;

		RedirectTarget(String baseClass, String contextClass, Method target, String newTargetName, Method[] conversionMethods) {
			if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
				this.baseClass = mapSlashedClass(baseClass);
				this.contextClass = mapSlashedClass(contextClass);
			} else {
				this.baseClass = baseClass;
				this.contextClass = contextClass;
			}

			this.target = target;
			this.newTargetName = newTargetName;
			this.conversionMethods = conversionMethods;
		}

		public boolean isConversionMethod(MethodInsnNode node) {
			for (Method conversionMethod : conversionMethods) {
				if (conversionMethod.matches(node)) {
					return true;
				}
			}

			return false;
		}
	}

	static class Method {
		public final String owner;
		public final String name;
		public final String desc;

		Method(String owner, String name, String desc) {
			if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
				this.owner = mapSlashedClass(owner);
				this.name = FabricLoader.getInstance().getMappingResolver()
						.mapMethodName("intermediary", owner.replace('/', '.'), name, desc);
				this.desc = mapDesc(desc);
			} else {
				this.owner = owner;
				this.name = name;
				this.desc = desc;
			}
		}

		public boolean matches(MethodInsnNode node) {
			return node.owner.equals(owner) && node.name.equals(name) && node.desc.equals(desc);
		}
	}
}
