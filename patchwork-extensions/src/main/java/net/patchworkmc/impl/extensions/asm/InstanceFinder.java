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

import java.util.Objects;
import java.util.Set;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.objectweb.asm.tree.analysis.SourceInterpreter;
import org.objectweb.asm.tree.analysis.SourceValue;

class InstanceFinder extends Analyzer<SourceValue> {
	private final MethodInsnNode target;

	public static void classLoadMePlease() {
		// InternalFrame isn't allowed to have static members so we have to do this instead.
		Objects.requireNonNull(InstanceFinder.InternalFrame.class);
	}

	InstanceFinder(MethodInsnNode target) {
		super(new SourceInterpreter());
		this.target = target;
	}

	private class InternalFrame extends Frame<SourceValue> {
		private AbstractInsnNode current;
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
		}

		@Override
		public SourceValue pop() {
			if (current == target) {
				throw new FoundException(super.pop().insns);
			}

			return super.pop();
		}
	}

	@Override
	protected Frame<SourceValue> newFrame(int numLocals, int numStack) {
		return new InternalFrame(numLocals, numStack);
	}

	class FoundException extends RuntimeException {
		public final Set<AbstractInsnNode> insns;

		FoundException(Set<AbstractInsnNode> insns) {
			this.insns = insns;
		}
	}
}
