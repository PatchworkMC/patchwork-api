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

package net.patchworkmc.impl.extensions.bakedmodel;

import java.util.Stack;
import java.util.function.Function;

import javax.annotation.Nonnull;

import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

/**
 * This class implements the emulation of passing an additional IModelData parameter to a Vanilla function,
 * and emulates a local variable.
 * setFuncParam(IModelData) should be called before calling the vanilla function.
 * Mixin the following into the Vanilla method:
 *
 * <br>
 * 1. HEAD: any of setupLocalVar().
 *
 * <br>
 * 2. RETURN: releaseLocalVar()
 *
 * <br>
 * To access the local variable, call getLocalVar() in between the above Mixins.
 */
public class ModelDataParameter {
	private final ThreadLocal<IModelData> modelDataParam = ThreadLocal.withInitial(() -> null);
	public static final IModelData DEFAULT = EmptyModelData.INSTANCE;

	/**
	 * Set the additional modelData parameter before calling the Vanilla method to emulate the IModelData sensitive version.
	 * If this is not called, the behavior of Vanilla method should not be changed, the additional parameter is assumed to
	 * have the default value: {@link #DEFAULT}.
	 */
	public void setFuncParam(@Nonnull IModelData modelData) {
		if (modelDataParam.get() != null) {
			throw new IllegalStateException("ModelDataContext is not clean!");
		} else {
			modelDataParam.set(modelData);
		}
	}

	public IModelData getFuncParamAndReset() {
		IModelData modelData = modelDataParam.get();
		return modelData == null ? DEFAULT : modelData;
	}

	private final ThreadLocal<Stack<IModelData>> modelDataStack = ThreadLocal.withInitial(Stack::new);

	public IModelData setupLocalVar(Function<IModelData, IModelData> mapper) {
		IModelData modelData = getFuncParamAndReset();
		modelData = mapper.apply(modelData);
		modelDataStack.get().push(modelData);
		return modelData;
	}

	public IModelData setupLocalVar() {
		return setupLocalVar(md -> md);
	}

	public IModelData getLocalVar() {
		return modelDataStack.get().lastElement();
	}

	public IModelData releaseLocalVar() {
		return modelDataStack.get().pop();
	}
}
