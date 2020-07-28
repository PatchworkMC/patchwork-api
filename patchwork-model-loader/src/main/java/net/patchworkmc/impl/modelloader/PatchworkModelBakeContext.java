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

import java.util.Stack;
import java.util.function.Function;

import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

/**
 * This class helps making model baking methods sensitive to custom textureGetter.
 */
public class PatchworkModelBakeContext {
	private static final Function<Identifier, Sprite> textureGetter_NotSet = (dummy) -> null;
	private final ThreadLocal<Function<Identifier, Sprite>> textureGetterParam = ThreadLocal.withInitial(() -> textureGetter_NotSet);
	private final ThreadLocal<Stack<Function<Identifier, Sprite>>> textureGetterStack = ThreadLocal.withInitial(Stack::new);

	//TODO: param VertexFormat is removed in 1.15
	private final ThreadLocal<VertexFormat> vertexFormatParam = ThreadLocal.withInitial(() -> null);
	private final ThreadLocal<Stack<VertexFormat>> vertexFormatStack = ThreadLocal.withInitial(Stack::new);

	/**
	 * This method should be called just before invoking the vanilla method.
	 *
	 * <p>This function is the only place to set textureGetterParam.
	 *
	 * <p>textureGetterParam is immediately consumed at the HEAD of the vanilla method.
	 */
	public void setExtraParam(Function<Identifier, Sprite> textureGetter, VertexFormat format) {
		textureGetterParam.set(textureGetter);
		vertexFormatParam.set(format);
	}

	public boolean isExtraParamSet() {
		return textureGetterParam != textureGetter_NotSet;
	}

	/**
	 * Called at the HEAD of the vanilla method and setup the context.
	 *
	 * <p>If the vanilla method is called directly, textureGetterParam is set to the NotSet marker,
	 * the default textureGetter will be used.
	 *
	 * <p>If the sensitive version is called,
	 * the extra textureGetter parameter is passed in textureGetterParam. After getting the parameter,
	 * textureGetterParam will be reset to NotSet marker immediately.
	 *
	 * <p>Then, the textureGetter is pushed to textureGetterStack.
	 *
	 * <p>The last textureGetter is popped from the stack when the constructor returns.
	 *
	 * @param defaultGetter the default textureGetter
	 */
	public void push(Function<Identifier, Sprite> defaultGetter, VertexFormat defaultformat) {
		Function<Identifier, Sprite> textureGetter = textureGetterParam.get();
		VertexFormat vertexFormat;

		if (textureGetter == textureGetter_NotSet) {
			textureGetter = defaultGetter;
			vertexFormat = defaultformat;
		} else {
			textureGetterParam.set(textureGetter_NotSet);
			vertexFormat = vertexFormatParam.get();
			vertexFormatParam.set(null);
		}

		textureGetterStack.get().push(textureGetter);
		vertexFormatStack.get().push(vertexFormat);
	}

	public Function<Identifier, Sprite> textureGetter() {
		return textureGetterStack.get().lastElement();
	}

	public VertexFormat vertexFormat() {
		return vertexFormatStack.get().lastElement();
	}

	public void pop() {
		textureGetterStack.get().pop();
	}
}
