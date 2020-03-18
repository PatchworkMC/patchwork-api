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

import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

@Mixin(AbstractInventoryScreen.class)
public abstract class MixinAbstractInventoryScreen extends ContainerScreen {
	@Shadow
	protected boolean offsetGuiForEffects;

	public MixinAbstractInventoryScreen(Container container, PlayerInventory playerInventory, Text name) {
		super(container, playerInventory, name);
	}

	@Inject(method = "method_2476", at = @At("RETURN"))
	private void potionShift(CallbackInfo info) {
		if (offsetGuiForEffects) {
			if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.PotionShiftEvent())) {
				this.x = (this.width - this.containerWidth) / 2;
			}
		}
	}
}
