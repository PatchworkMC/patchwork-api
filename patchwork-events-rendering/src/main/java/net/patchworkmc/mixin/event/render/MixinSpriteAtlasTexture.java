package net.patchworkmc.mixin.event.render;

import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.patchworkmc.impl.event.render.RenderEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Set;

@Mixin(SpriteAtlasTexture.class)
public class MixinSpriteAtlasTexture {
	@Inject(method = "stitch", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	private void onStitch(ResourceManager resourceManager, Iterable<Identifier> iterable, Profiler profiler, CallbackInfoReturnable<SpriteAtlasTexture.Data> cir, Set<Identifier> set) {
		RenderEvents.onTextureStitchPre((SpriteAtlasTexture) (Object) this, set);
	}

	@Inject(method = "upload", at = @At("RETURN"))
	private void onUpload(SpriteAtlasTexture.Data data, CallbackInfo ci) {
		RenderEvents.onTextureStitchPost((SpriteAtlasTexture) (Object) this);
	}
}
