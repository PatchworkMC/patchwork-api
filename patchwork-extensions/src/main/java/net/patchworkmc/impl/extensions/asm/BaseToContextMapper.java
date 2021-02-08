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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.objectweb.asm.tree.analysis.SourceInterpreter;
import org.objectweb.asm.tree.analysis.SourceValue;

// TODO: this doesn't work on ChunkRegion.setBlockState's call to createBlockEntity, for no apparent reason.
class BaseToContextMapper extends Analyzer<SourceValue> {
	public final Map<Integer, Integer> translations = new HashMap<>();
	private final Transformer.RedirectTarget redirect;

	BaseToContextMapper(Transformer.RedirectTarget redirect) {
		super(new SourceInterpreter());
		this.redirect = redirect;
	}

	public static void classLoadMePlease() {
		// InternalFrame isn't allowed to have static members so we have to do this instead.
		Objects.requireNonNull(InternalFrame.class);
	}

	private class InternalFrame extends Frame<SourceValue> {
		private AbstractInsnNode current;
		// base would be block and context would be BlockState
		private SourceValue base = null;
		private SourceValue contextCandidate = null;
		private SourceValue context = null;
		private int contextIndex = -1;

		InternalFrame(int numLocals, int numStack) {
			super(numLocals, numStack);
		}

		@Override
		public void execute(AbstractInsnNode insn, Interpreter<SourceValue> interpreter) throws AnalyzerException {
			this.current = insn;

			super.execute(insn, interpreter);
		}

		@Override
		public void push(SourceValue value) {
			super.push(value);

			if (current.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				MethodInsnNode node = (MethodInsnNode) current;

				if (redirect.isConversionMethod(node)) {
					base = value;
				}
			} else if (current.getOpcode() == Opcodes.ALOAD) {
				VarInsnNode node = (VarInsnNode) current;
				contextCandidate = value;
				contextIndex = node.var;
			}
		}

		@Override
		public SourceValue pop() {
			SourceValue value = super.pop();

			// if we're popping our context candidate for a conversion method, then we know we have it!
			if (value == contextCandidate && current.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				MethodInsnNode node = (MethodInsnNode) current;

				if (redirect.isConversionMethod(node)) {
					context = contextCandidate;
				}

				return value;
			}

			// If we are popping our blockTarget into a field, and we have a state, then we're golden.
			if (base != null && context != null && value == base && current.getOpcode() == Opcodes.ASTORE) {
				Integer ret = translations.put(((VarInsnNode) current).var, contextIndex);

				if (ret != null && contextIndex != ret) {
					// TODO: there might be a case where this fails even with expand frames,
					//  but the chance is so astronomically low it's not worth investigating at this time
					throw new AssertionError("contextIndex is duplicated in translations!");
				}

				context = null;
			}

			return value;
		}
	}

	@Override
	protected Frame<SourceValue> newFrame(int numLocals, int numStack) {
		return new InternalFrame(numLocals, numStack);
	}
}
