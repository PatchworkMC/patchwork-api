package net.patchworkmc.mixin.event.world;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.Difficulty;

import net.patchworkmc.impl.event.world.WorldEvents;

@Mixin(ClientWorld.Properties.class)
public abstract class MixinClientWorldProperties {
	@Shadow
	private Difficulty difficulty;

	@Inject(method = "setDifficulty", at = @At("HEAD"))
	private void onDifficultyChange(Difficulty difficulty, CallbackInfo info) {
		WorldEvents.onDifficultyChange(difficulty, this.difficulty);
	}
}
