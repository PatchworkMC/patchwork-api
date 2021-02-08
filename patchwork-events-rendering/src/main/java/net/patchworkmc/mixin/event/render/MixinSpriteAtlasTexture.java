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

package net.patchworkmc.mixin.event.render;

import java.util.Set;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import net.patchworkmc.impl.event.render.RenderEvents;

@Mixin(SpriteAtlasTexture.class)
public class MixinSpriteAtlasTexture {
	@Inject(
			method = "stitch", at = @At(
				value = "INVOKE_STRING",
				target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
				args = "ldc=extracting_frames",
				shift = At.Shift.AFTER,
				ordinal = 0
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void onStitch(ResourceManager resourceManager, Stream<Identifier> idStream, Profiler profiler, int mipmapLevel, CallbackInfoReturnable<SpriteAtlasTexture.Data> cir, Set<Identifier> set) {
		RenderEvents.onTextureStitchPre((SpriteAtlasTexture) (Object) this, set);
	}

	@Inject(method = "upload", at = @At("TAIL"))
	private void onUpload(SpriteAtlasTexture.Data data, CallbackInfo ci) {
		RenderEvents.onTextureStitchPost((SpriteAtlasTexture) (Object) this);
	}
}
