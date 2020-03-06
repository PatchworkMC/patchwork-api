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

package com.patchworkmc.mixin.registries;

import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.GLX;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.util.Identifier;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
	@Shadow
	protected abstract void loadShader(Identifier identifier);

	@Shadow
	private ShaderEffect shader;

	@Inject(method = "onCameraEntitySet", at = @At("RETURN"))
	private void onCameraEntitySet(@Nullable Entity entity, CallbackInfo ci) {
		if (GLX.usePostProcess) {
			if (shader == null && entity != null && !(entity instanceof CreeperEntity) && !(entity instanceof SpiderEntity) && !(entity instanceof EndermanEntity)) {
				Identifier shader = ClientRegistry.getEntityShader(entity.getClass());

				if (shader != null) {
					loadShader(shader);
				}
			}
		}
	}
}