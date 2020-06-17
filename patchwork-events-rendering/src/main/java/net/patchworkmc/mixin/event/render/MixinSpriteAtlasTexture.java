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
	@Inject(method = "stitch", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/SpriteAtlasTexture;loadSprites(Lnet/minecraft/resource/ResourceManager;Ljava/util/Set;)Ljava/util/Collection;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	private void onStitch(ResourceManager resourceManager, Iterable<Identifier> iterable, Profiler profiler, CallbackInfoReturnable<SpriteAtlasTexture.Data> cir, Set<Identifier> set) {
		RenderEvents.onTextureStitchPre((SpriteAtlasTexture) (Object) this, set);
	}

	@Inject(method = "upload", at = @At("RETURN"))
	private void onUpload(SpriteAtlasTexture.Data data, CallbackInfo ci) {
		RenderEvents.onTextureStitchPost((SpriteAtlasTexture) (Object) this);
	}
}
