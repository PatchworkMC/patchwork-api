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