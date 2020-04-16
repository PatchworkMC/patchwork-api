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

package net.patchworkmc.mixin.levelgenerators;

import net.minecraftforge.common.extensions.IForgeWorldType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.world.level.LevelGeneratorType;

import net.patchworkmc.api.levelgenerators.PatchworkLevelGeneratorType;

@Mixin(CreateWorldScreen.class)
public abstract class MixinCreateWorldScreen extends Screen {
	protected MixinCreateWorldScreen(Text title) {
		super(title);
	}

	@Shadow
	private int generatorType;

	@Inject(at = @At("RETURN"), method = "method_19926")
	private void onCustomizeButton(ButtonWidget widget, CallbackInfo info) {
		LevelGeneratorType generatorType = LevelGeneratorType.TYPES[this.generatorType];

		if (generatorType instanceof PatchworkLevelGeneratorType) {
			((IForgeWorldType) generatorType).onCustomizeButton(this.minecraft, (CreateWorldScreen) (Object) this);
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "org/apache/commons/lang3/StringUtils.isEmpty(Ljava/lang/CharSequence;)Z"), method = "createLevel")
	private void onGUICreateWorldPress(CallbackInfo info) {
		LevelGeneratorType generatorType = LevelGeneratorType.TYPES[this.generatorType];

		if (generatorType instanceof PatchworkLevelGeneratorType) {
			((IForgeWorldType) generatorType).onGUICreateWorldPress();
		}
	}
}
