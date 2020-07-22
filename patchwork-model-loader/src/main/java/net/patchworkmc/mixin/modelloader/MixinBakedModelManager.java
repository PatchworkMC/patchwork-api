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

package net.patchworkmc.mixin.modelloader;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import net.patchworkmc.impl.modelloader.ModelEventDispatcher;
import net.patchworkmc.impl.modelloader.Signatures;

@Mixin(BakedModelManager.class)
public abstract class MixinBakedModelManager {
	@Shadow
	private Map<Identifier, BakedModel> models;

	@Redirect(method = "prepare", at = @At(value = "NEW", target = Signatures.ModelLoader_new, ordinal = 0))
	private ModelLoader patchwork_prepare_new_ModelLoader(ResourceManager resourceManager, SpriteAtlasTexture spriteAtlas, BlockColors blockColors, Profiler profiler) {
		return new net.minecraftforge.client.model.ModelLoader(resourceManager, spriteAtlas, blockColors, profiler);
	}

	@Inject(method = "apply", at = @At(shift = Shift.BEFORE, value = "INVOKE", target = Signatures.Profiler_swap, ordinal = 0))
	protected void patchwork_apply_swap(ModelLoader modelLoader, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
		BakedModelManager me = (BakedModelManager) (Object) this;
		ModelEventDispatcher.onModelBake(me, models, (net.minecraftforge.client.model.ModelLoader) modelLoader);
	}
}
