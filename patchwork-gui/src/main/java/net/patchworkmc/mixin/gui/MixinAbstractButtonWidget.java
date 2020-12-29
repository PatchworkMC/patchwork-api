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

package net.patchworkmc.mixin.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AbstractButtonWidget;

import net.patchworkmc.impl.gui.ForgeAbstractButtonWidget;

@Mixin(AbstractButtonWidget.class)
public abstract class MixinAbstractButtonWidget extends DrawableHelper implements ForgeAbstractButtonWidget {
	@Shadow
	protected int height;

	@Shadow
	public boolean active;

	@Unique
	private static final int UNSET_FG_COLOR = -1;

	@Unique
	protected int packedFGColor = UNSET_FG_COLOR;

	@ModifyVariable(method = "renderButton(Lnet/minecraft/client/util/math/MatrixStack;IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/AbstractButtonWidget;getMessage()Lnet/minecraft/text/Text;", ordinal = 0), ordinal = 2)
	private int hookFGColor(int original) {
		return getFGColor();
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public void setHeight(int value) {
		this.height = value;
	}

	@Override
	public int getFGColor() {
		if (packedFGColor != UNSET_FG_COLOR) return packedFGColor;
		return this.active ? 16777215 : 10526880; // White : Light Grey
	}

	@Override
	public void setFGColor(int color) {
		this.packedFGColor = color;
	}

	@Override
	public void clearFGColor() {
		this.packedFGColor = UNSET_FG_COLOR;
	}
}
